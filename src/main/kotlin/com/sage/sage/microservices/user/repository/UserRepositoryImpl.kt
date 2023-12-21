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
import com.sage.sage.microservices.user.model.OTPModel
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.request.UserRegistration.Companion.toUserRegistration
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceRequest
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Repository
import kotlin.random.Random


@Repository
class UserRepositoryImpl(
    private val azureInitializer: AzureInitializer,
    private val emailSender: JavaMailSender
) : UserRepository {

    val profileUserKey = "profile"
    val deviceUserKey = "device"
    val otpUserKey = "otp"

    override fun create(userRegistrationRequest: UserRegistrationRequestV2): Int? {
        val user = userRegistrationRequest.toUserRegistration()
        user.userKey = profileUserKey
        user.isVerified = false
        val response = azureInitializer.userContainer?.createItem(
            user,
            PartitionKey(user.userKey),
            CosmosItemRequestOptions()
        )

        createDevice(
            DeviceModel(
                user.devices[0].deviceId,
                deviceUserKey,
                user.id
            )
        )

        return response?.statusCode
    }

    override fun createDevice(deviceModel: DeviceModel): String {
        val response = azureInitializer.userContainer?.createItem(
            deviceModel,
            PartitionKey(deviceModel.userKey),
            CosmosItemRequestOptions()
        )

        return response?.statusCode.toString()
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

    override fun getProfileByUserId(userId: String): User? {
        val response = azureInitializer.userContainer?.readItem(
            userId,
            PartitionKey(profileUserKey),
            User::class.java
        )

        return response?.item
    }


    override fun sendOtp(id: String, email: String): String {
        val otp = generateSixDigitOTP()

        val message = SimpleMailMessage()
        message.setFrom("noreply@kosha.com")
        message.setTo(email)
        message.setSubject(EmailTemplateConstants.VERIFICATION_EMAIL_SUBJECT)
        message.setText(EmailTemplateConstants.VERIFICATION_EMAIL_BODY.format(otp))
        emailSender.send(message)

        val otpresponse = saveOtp(id, email, otp)
        println("User OTP: $otp code: $otpresponse")

        return otp
    }

    private fun saveOtp(id: String ,email: String, otp: String): String {
        val otpModel = OTPModel(id,otpUserKey, email, otp)
        val response = azureInitializer.userContainer?.createItem(
            otpModel,
            PartitionKey(otpUserKey),
            CosmosItemRequestOptions()
        )
        return response?.statusCode.toString()
    }

    override fun registrationCancelled(email: String) {
        azureInitializer.userContainer?.deleteItem(
            email,
            PartitionKey(otpUserKey),
            CosmosItemRequestOptions()
        )
    }

    override fun otpVerification(id: String, request: UserVerificationRequest): Boolean {
        val response = azureInitializer.userContainer?.readItem(
            id,
            PartitionKey(otpUserKey),
            OTPModel::class.java
        )

        //bypass otp
//        return request.otp == "12345"
        return response?.item?.otp == request.otp
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

    override fun getOtpById(id: String): OTPModel? {
        return azureInitializer.userContainer?.readItem(
            id,
            PartitionKey(otpUserKey),
            OTPModel::class.java
        )?.item
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

    override fun updateOtp(id: String, otp: String): String {
        val response = azureInitializer.userContainer?.patchItem(
            id,
            PartitionKey(otpUserKey),
            CosmosPatchOperations.create()
                .replace("/otp", otp), OTPModel::class.java
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

