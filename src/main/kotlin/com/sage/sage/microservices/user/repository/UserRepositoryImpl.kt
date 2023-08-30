package com.sage.sage.microservices.user.repository

import com.azure.communication.email.models.EmailMessage
import com.azure.communication.email.models.EmailSendResult
import com.azure.communication.email.models.EmailSendStatus
import com.azure.core.util.polling.LongRunningOperationStatus
import com.azure.core.util.polling.PollResponse
import com.azure.core.util.polling.SyncPoller
import com.azure.cosmos.models.CosmosItemRequestOptions
import com.azure.cosmos.models.CosmosPatchOperations
import com.azure.cosmos.models.PartitionKey
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.WriteResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient
import com.sage.sage.microservices.azure.AzureInitializer
import com.sage.sage.microservices.device.repository.DevicesDatabaseConstants.DATABASE_DEVICES_COLLECTION
import com.sage.sage.microservices.user.model.UserV2
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceModelV2
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.DATABASE_COLLECTION
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_EMAIL
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_NAME
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_OTP
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_PASSWORD
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_SURNAME
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_VERIFICATION
import org.apache.http.client.HttpResponseException
import org.springframework.stereotype.Repository
import kotlin.random.Random


@Repository
class UserRepositoryImpl(
    private val azureInitializer: AzureInitializer
) : UserRepository {

    val profileUserKey = "profile"
    val deviceUserKey = "device"

    override fun create(userRegistrationRequest: UserRegistrationRequestV2): Pair<Int?, String?> {
        userRegistrationRequest.otp = generateSixDigitOTP()
        userRegistrationRequest.userKey = profileUserKey
        userRegistrationRequest.isVerified = false
        val response = azureInitializer.userContainer?.createItem(
            userRegistrationRequest,
            PartitionKey(userRegistrationRequest.userKey),
            CosmosItemRequestOptions()
        )

        createDevice(DeviceModelV2(
            userRegistrationRequest.devices[0].deviceId,
            deviceUserKey,
            true,
            userRegistrationRequest.id)
        )

        return Pair(response?.statusCode, userRegistrationRequest.otp)
    }

    override fun createDevice(deviceModel: DeviceModelV2): String {
       val response = azureInitializer.userContainer?.createItem(
            deviceModel,
            PartitionKey(deviceModel.userKey),
            CosmosItemRequestOptions()
        )

        return  response?.statusCode.toString()
    }

    override fun addDevice(username: String, deviceModel: DeviceModel): Int? {
        val user = getByUsername(username)
        val devices = ArrayList<DeviceModel>()
        user?.devices?.let { devices.addAll(it) }
        devices.add(deviceModel)
        val response = azureInitializer.userContainer?.patchItem(
            username,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/devices", devices as List<DeviceModel>), UserV2::class.java
        )

        return response?.statusCode
    }


    override fun sendOtp(email: String, otp: String) {
        println("Email send started")
        val message: EmailMessage = EmailMessage()
            .setSenderAddress("<DoNotReply@0d93d82e-7c88-483e-be50-49579c43b2dd.azurecomm.net>")
            .setToRecipients("<$email>")
            .setSubject(EmailTemplateConstants.VERIFICATION_EMAIL_SUBJECT)
            .setBodyPlainText(EmailTemplateConstants.VERIFICATION_EMAIL_BODY.format(otp))

        try {
            val poller: SyncPoller<EmailSendResult?, EmailSendResult> = azureInitializer.emailClient!!.beginSend(message, null)
            var pollResponse: PollResponse<EmailSendResult?>? = null
            while (pollResponse == null || pollResponse.status === LongRunningOperationStatus.NOT_STARTED || pollResponse.status === LongRunningOperationStatus.IN_PROGRESS) {
                pollResponse = poller.poll()
                println("Email send poller status: " + pollResponse.status)
            }
            if (poller.finalResult.status === EmailSendStatus.SUCCEEDED) {
                System.out.printf("Successfully sent the email (operation id: %s)", poller.finalResult.id)
            } else {
                throw RuntimeException(poller.finalResult.error.message)
            }
        } catch (exception: Exception) {
            println(exception.message)
        }
    }

    override fun otpVerification(username: String, request: UserVerificationRequest): Boolean {
        val user = getByUsername(username)
        val result: Boolean
        if (user != null) {
            if (user.otp == request.otp) {
                result = true
                updateVerification(username, true)
            } else {
                result = false
            }
        } else {
            result = false
        }

        return result
    }

    private fun generateSixDigitOTP(): String {
        val otpLength = 6
        val otpBuilder = StringBuilder()

        repeat(otpLength) {
            val randomDigit = Random.nextInt(0, 10)
            otpBuilder.append(randomDigit)
        }

        return otpBuilder.toString()
    }

    override fun getByUsername(username: String): UserV2? {
        return azureInitializer.userContainer?.readItem(
            username,
            PartitionKey(profileUserKey),
            UserV2::class.java
        )?.item
    }

    override fun deleteByUsername(username: String): String {
        val database = FirestoreClient.getFirestore()
        val writeResult: ApiFuture<WriteResult> = database.collection(DATABASE_COLLECTION).document(username).delete()
        return writeResult.get().updateTime.toString()
    }

    override fun updateName(username: String, userUpdateNameRequest: UserUpdateNameRequest): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        val future = docRef.update(USER_DATABASE_NAME, userUpdateNameRequest.newName)
        val result: WriteResult = future.get()
        return result.toString()
    }

    private fun updateVerification(
        username: String, isVerified: Boolean
    ): String {
       val response = azureInitializer.userContainer?.patchItem(
           username,
           PartitionKey(profileUserKey),
           CosmosPatchOperations.create()
               .replace("/verified", isVerified)
               .replace("/otp", ""), UserV2::class.java
       )

        return response?.statusCode.toString()
    }

    override fun updateSurname(username: String, userUpdateNameRequest: UserUpdateSurnameRequest): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        val future = docRef.update(USER_DATABASE_SURNAME, userUpdateNameRequest.newSurname)
        val result: WriteResult = future.get()
        return result.toString()
    }

    override fun updateEmail(username: String, userUpdateNameRequest: UserUpdateEmailRequest): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        val future = docRef.update(USER_DATABASE_EMAIL, userUpdateNameRequest.newEmail)
        val result: WriteResult = future.get()
        return result.toString()
    }

    override fun updatePassword(username: String, userUpdateNameRequest: UserUpdatePasswordRequest): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        val future = docRef.update(USER_DATABASE_PASSWORD, userUpdateNameRequest.newPassword)
        val result: WriteResult = future.get()
        return result.toString()
    }


}

