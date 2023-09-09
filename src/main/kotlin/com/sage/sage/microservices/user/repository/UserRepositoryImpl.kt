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
import com.sage.sage.microservices.user.model.UserV2
import com.sage.sage.microservices.user.model.request.*
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

    override fun addDevice(username: String, deviceModel: DeviceRequest): Int? {
        val user = getByUsername(username)
        val devices = ArrayList<DeviceRequest>()
        user?.devices?.let { devices.addAll(it) }
        devices.add(deviceModel)
        val response = azureInitializer.userContainer?.patchItem(
            username,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/devices", devices as List<DeviceRequest>), UserV2::class.java
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
    }

    override fun otpVerification(username: String, request: UserVerificationRequest): Boolean {
        val user = getByUsername(username)
        val result: Boolean
        if (user != null) {
            if (user.otp == request.otp) {
                result = true
                updateVerification(username)
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

    override fun deleteByUsername(username: String): Int? {
       return azureInitializer.userContainer?.deleteItem(
            username,
            PartitionKey(profileUserKey),
           CosmosItemRequestOptions()
        )?.statusCode
    }

    override fun updateName(username: String, userUpdateNameRequest: UserUpdateNameRequest): String {
        val response = azureInitializer.userContainer?.patchItem(
            username,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/name", userUpdateNameRequest.newName), UserV2::class.java
        )

        return response?.statusCode.toString()
    }

    private fun updateVerification(username: String): String {
       val response = azureInitializer.userContainer?.patchItem(
           username,
           PartitionKey(profileUserKey),
           CosmosPatchOperations.create()
               .replace("/verified", true)
               .replace("/otp", ""), UserV2::class.java
       )

        return response?.statusCode.toString()
    }

    override fun updateOtp(username: String, otp: String): String {
        val response = azureInitializer.userContainer?.patchItem(
            username,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/otp", otp), UserV2::class.java
        )

        return response?.statusCode.toString()
    }

    override fun updateSurname(username: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): String {
        val response = azureInitializer.userContainer?.patchItem(
            username,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/surname", userUpdateSurnameRequest.newSurname), UserV2::class.java
        )

        return response?.statusCode.toString()
    }

    override fun updateEmail(username: String, userUpdateEmailRequest: UserUpdateEmailRequest): String {
        val response = azureInitializer.userContainer?.patchItem(
            username,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/email", userUpdateEmailRequest.newEmail), UserV2::class.java
        )

        return response?.statusCode.toString()
    }

    override fun updatePassword(username: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): String {
        val response = azureInitializer.userContainer?.patchItem(
            username,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/password", userUpdatePasswordRequest.newPassword), UserV2::class.java
        )

        return response?.statusCode.toString()
    }


}

