package com.sage.sage.microservices.user.repository

import com.azure.core.util.polling.SyncPoller
import com.azure.cosmos.CosmosException
import com.azure.cosmos.models.CosmosItemRequestOptions
import com.azure.cosmos.models.CosmosPatchOperations
import com.azure.cosmos.models.PartitionKey
import com.sage.sage.microservices.azure.AzureInitializer
import com.sage.sage.microservices.exception.KoshaGatewayException
import com.sage.sage.microservices.exception.McaHttpResponseCode
import com.sage.sage.microservices.user.model.OTPModel
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.request.UserRegistration.Companion.toUserRegistration
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceRequest
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import kotlin.random.Random


@Repository
class UserRepositoryImpl(
    private val azureInitializer: AzureInitializer,
    private val emailSender: JavaMailSender
) : UserRepository {

    val profileUserKey = "profile"
    val deviceUserKey = "device"
    val otpUserKey = "otp"

    override fun create(userRegistrationRequest: UserRegistrationRequestV2): Mono<DeviceModel> {
        val user = userRegistrationRequest.toUserRegistration()
        user.userKey = profileUserKey
        user.isVerified = false
        azureInitializer.userContainer?.createItem(
            user,
            PartitionKey(user.userKey),
            CosmosItemRequestOptions()
        )
        return Mono.just(
            DeviceModel(
                user.devices[0].deviceId,
                deviceUserKey,
                user.id
            )
        )
    }

    override fun createDevice(deviceModel: DeviceModel): Mono<Void> {
        azureInitializer.userContainer?.createItem(
            deviceModel,
            PartitionKey(deviceModel.userKey),
            CosmosItemRequestOptions()
        )

        return Mono.empty()
    }

    override fun addDevice(email: String, deviceModel: DeviceRequest): Mono<Void> {
        return getByEmail(email).flatMap { user ->
            val devices = ArrayList<DeviceRequest>()
            devices.addAll(user.devices)
            devices.add(deviceModel)
            azureInitializer.userContainer?.patchItem(
                user?.id,
                PartitionKey(profileUserKey),
                CosmosPatchOperations.create()
                    .replace("/devices", devices as List<DeviceRequest>), User::class.java
            )
            Mono.empty()
        }
    }

    override fun getProfileByUserId(userId: String): Mono<User> {
        val response = azureInitializer.userContainer?.readItem(
            userId,
            PartitionKey(profileUserKey),
            User::class.java
        )
        return Mono.justOrEmpty(response?.item)
    }


    override fun sendOtp(id: String, email: String): Mono<String> {
        val otp = generateSixDigitOTP()
        val message = SimpleMailMessage()
        message.from = "noreply@kosha.com"
        message.setTo(email)
        message.subject = EmailTemplateConstants.VERIFICATION_EMAIL_SUBJECT
        message.text = EmailTemplateConstants.VERIFICATION_EMAIL_BODY.format(otp)
        emailSender.send(message)

        return saveOtp(id, email, otp).flatMap {
            Mono.just(it)
        }
    }

    private fun saveOtp(id: String, email: String, otp: String): Mono<String> {
        val otpModel = OTPModel(id, otpUserKey, email, otp)
        azureInitializer.userContainer?.createItem(
            otpModel,
            PartitionKey(otpUserKey),
            CosmosItemRequestOptions()
        )
        return Mono.just(id)
    }

    override fun registrationCancelled(email: String) {
        azureInitializer.userContainer?.deleteItem(
            email,
            PartitionKey(otpUserKey),
            CosmosItemRequestOptions()
        )
    }

    override fun otpVerification(id: String, request: UserVerificationRequest): Mono<Boolean> {
        val response = azureInitializer.userContainer?.readItem(
            id,
            PartitionKey(otpUserKey),
            OTPModel::class.java
        )
        //bypass otp
//        return request.otp == "12345"
        return Mono.just(response?.item?.otp == request.otp)
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

    override fun getByEmail(email: String): Mono<User> {
        val user = azureInitializer.userContainer?.readAllItems(
            PartitionKey(profileUserKey),
            User::class.java
        )?.find {
            it.email == email
        }

        return Mono.justOrEmpty(user)
    }

    override fun getOtpById(id: String): Mono<OTPModel> {
        val response = azureInitializer.userContainer?.readItem(
            id,
            PartitionKey(otpUserKey),
            OTPModel::class.java
        )?.item

        return Mono.justOrEmpty(response)
    }

    override fun deleteByEmail(email: String): Mono<Void> {
        return getByEmail(email).flatMap { user ->
            azureInitializer.userContainer?.deleteItem(
                user?.id,
                PartitionKey(profileUserKey),
                CosmosItemRequestOptions()
            )
            Mono.empty()
        }
    }

    override fun updateName(email: String, userUpdateNameRequest: UserUpdateNameRequest): Mono<Void> {
        return getByEmail(email).flatMap { user ->
            azureInitializer.userContainer?.patchItem(
                user?.id,
                PartitionKey(profileUserKey),
                CosmosPatchOperations.create()
                    .replace("/name", userUpdateNameRequest.newName), User::class.java
            )
            Mono.empty()
        }
    }

    override fun updateOtp(id: String, otp: String): Mono<Void> {
        azureInitializer.userContainer?.patchItem(
            id,
            PartitionKey(otpUserKey),
            CosmosPatchOperations.create()
                .replace("/otp", otp), OTPModel::class.java
        )
        return Mono.empty()
    }

    override fun updateSurname(email: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): Mono<Void> {
        return getByEmail(email).flatMap { user ->
            azureInitializer.userContainer?.patchItem(
                user?.id,
                PartitionKey(profileUserKey),
                CosmosPatchOperations.create()
                    .replace("/surname", userUpdateSurnameRequest.newSurname), User::class.java
            )
            Mono.empty()
        }
    }

    override fun updateEmail(email: String, userUpdateEmailRequest: UserUpdateEmailRequest): Mono<Void> {
        return getByEmail(email).flatMap { user ->
            azureInitializer.userContainer?.patchItem(
                user?.id,
                PartitionKey(profileUserKey),
                CosmosPatchOperations.create()
                    .replace("/email", userUpdateEmailRequest.newEmail), User::class.java
            )
            Mono.empty()
        }
    }

    override fun updatePassword(email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): Mono<Void> {
        return getByEmail(email).flatMap { user ->
            azureInitializer.userContainer?.patchItem(
                user?.id,
                PartitionKey(profileUserKey),
                CosmosPatchOperations.create()
                    .replace("/password", userUpdatePasswordRequest.newPassword), User::class.java
            )
            Mono.empty()
        }
    }

}

