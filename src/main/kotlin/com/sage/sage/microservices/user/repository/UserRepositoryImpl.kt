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
import com.sage.sage.microservices.azure.AzureInitializer
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.request.UserRegistration.Companion.toUserRegistration
import com.sage.sage.microservices.user.model.response.DeviceModelV2
import com.sage.sage.microservices.user.model.response.DeviceRequest
import org.springframework.stereotype.Repository
import kotlin.random.Random


@Repository
class UserRepositoryImpl(
    private val azureInitializer: AzureInitializer
) : UserRepository {

    val profileUserKey = "profile"
    val deviceUserKey = "device"

    override fun create(userRegistrationRequest: UserRegistrationRequestV2): Pair<Int?, String?> {
        val user = userRegistrationRequest.toUserRegistration()
        user.otp = generateSixDigitOTP()
        user.userKey = profileUserKey
        user.isVerified = false
        val response = azureInitializer.userContainer?.createItem(
            user,
            PartitionKey(user.userKey),
            CosmosItemRequestOptions()
        )

        createDevice(DeviceModelV2(
            user.devices[0].deviceId,
            deviceUserKey,
            false,
            user.id)
        )

        return Pair(response?.statusCode, user.otp)
    }

    override fun createDevice(deviceModel: DeviceModelV2): String {
       val response = azureInitializer.userContainer?.createItem(
            deviceModel,
            PartitionKey(deviceModel.userKey),
            CosmosItemRequestOptions()
        )

        return  response?.statusCode.toString()
    }

    override fun addDevice(email: String, deviceModel: DeviceRequest): Int? {
        val user = getByEmail(email)
        val devices = ArrayList<DeviceRequest>()
        user?.devices?.let { devices.addAll(it) }
        devices.add(deviceModel)
        val response = azureInitializer.userContainer?.patchItem(
            user?.id,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/devices", devices as List<DeviceRequest>), User::class.java
        )

        return response?.statusCode
    }

    override fun resendOtp(email: String): String {
        val otp = generateSixDigitOTP()
        sendOtp(email, otp)
        return otp
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

    override fun otpVerification(email: String, request: UserVerificationRequest): Boolean {
        val user = getByEmail(email)
        val result: Boolean
        if (user != null) {
            if (user.otp == request.otp) {
                result = true
                updateVerification(email)
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

    override fun getByEmail(email: String): User? {
        return azureInitializer.userContainer?.readAllItems(
            PartitionKey(profileUserKey),
            User::class.java
        )?.find {
            it.email == email
        }
    }

    override fun deleteByEmail(email: String): Int? {
        val user = getByEmail(email)
       return azureInitializer.userContainer?.deleteItem(
            user?.id,
            PartitionKey(profileUserKey),
           CosmosItemRequestOptions()
        )?.statusCode
    }

    override fun updateName(email: String, userUpdateNameRequest: UserUpdateNameRequest): String {
        val user = getByEmail(email)
        val response = azureInitializer.userContainer?.patchItem(
            user?.id,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/name", userUpdateNameRequest.newName), User::class.java
        )

        return response?.statusCode.toString()
    }

    private fun updateVerification(email: String): String {
        val user = getByEmail(email)
       val response = azureInitializer.userContainer?.patchItem(
           user?.id,
           PartitionKey(profileUserKey),
           CosmosPatchOperations.create()
               .replace("/verified", true)
               .replace("/otp", ""), User::class.java
       )

        return response?.statusCode.toString()
    }

    override fun updateOtp(email: String, otp: String): String {
        val user = getByEmail(email)
        val response = azureInitializer.userContainer?.patchItem(
            user?.id,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/otp", otp), User::class.java
        )

        return response?.statusCode.toString()
    }

    override fun updateSurname(email: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): String {
        val user = getByEmail(email)
        val response = azureInitializer.userContainer?.patchItem(
            user?.id,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/surname", userUpdateSurnameRequest.newSurname), User::class.java
        )

        return response?.statusCode.toString()
    }

    override fun updateEmail(email: String, userUpdateEmailRequest: UserUpdateEmailRequest): String {
        val user = getByEmail(email)
        val response = azureInitializer.userContainer?.patchItem(
            user?.id,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/email", userUpdateEmailRequest.newEmail), User::class.java
        )

        return response?.statusCode.toString()
    }

    override fun updatePassword(email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): String {
        val user = getByEmail(email)
        val response = azureInitializer.userContainer?.patchItem(
            user?.id,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/password", userUpdatePasswordRequest.newPassword), User::class.java
        )

        return response?.statusCode.toString()
    }


}

