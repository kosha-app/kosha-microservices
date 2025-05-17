package com.sage.sage.microservices.user.service

import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import com.sage.sage.microservices.exception.exceptionobjects.McaHttpResponseCode
import com.sage.sage.microservices.featuretoggles.KoshaProdFeatureToggles
import com.sage.sage.microservices.user.email.EmailService
import com.sage.sage.microservices.user.model.OTPModel
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.User.Companion.toMono
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.*
import com.sage.sage.microservices.user.model.response.GetUserInfoResponse.Companion.toGetUserInfo
import com.sage.sage.microservices.user.repository.OtpRepository
import com.sage.sage.microservices.user.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID
import kotlin.random.Random

@Service
class UserService(
    private val userRepository: UserRepository,
    private val otpRepository: OtpRepository,
    private val emailService: EmailService,
    private val featureToggles: KoshaProdFeatureToggles
) {

    fun create(request: UserRegistrationRequestV2): Mono<Void> {
        val userId = UUID.randomUUID().toString()
        return userRepository.save(
            User(
                userId = userId,
                email = request.email,
                name = request.name,
                password = request.password,
                dateOfBirth = request.dateOfBirth,
                gender = request.gender,
                cellNumber = request.cellNumber,
                devices = listOf(DeviceModel(request.deviceId, userId))
            )
        ).toMono().flatMap {
            Mono.empty()
        }
    }

    fun checkEmail(email: String): Mono<CheckEmailResponse> {
        val exists = userRepository.existsByEmail(email)
        return if (exists) {
            Mono.error(
                KoshaGatewayException(
                    McaHttpResponseCode.ERROR_ITEM_ALREADY_EXISTS,
                    "User with email $email already exists"
                )
            )
        } else {
            val otp = generateSixDigitOTP()
            sendOtp(otp, email)
            saveOtp(UUID.randomUUID().toString(), otp)
                .flatMap { id ->
                    Mono.just(CheckEmailResponse(id))
                }

        }
    }

    fun sendOtp(otp: String, email: String) {
        emailService.sendOtpEmail(email, otp, 5)
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

    private fun saveOtp(id: String, otp: String): Mono<String> {
        val otpModel = otpRepository.save(OTPModel(id, otp))
        return Mono.just(otpModel.id)
    }

    fun otpVerification(id: String, request: UserVerificationRequest): Mono<Void> {
        return verifyOtp(id, request).flatMap { isVerified ->
            if (isVerified) {
                Mono.empty()
            } else {
                Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_UNAUTHORISED, "OTP does not match"))
            }
        }
    }

    private fun verifyOtp(id: String, request: UserVerificationRequest): Mono<Boolean> {
        val otp = otpRepository.getReferenceById(id)

        return if (featureToggles.otpBypass) {
            Mono.just(request.otp == "123456" || otp.otp == request.otp)
        } else {
            Mono.just(otp.otp == request.otp)
        }
    }


    fun signUserIn(userSignInRequest: UserSignInRequest): Mono<DefaultResponse> {
        if (userRepository.existsByEmail(userSignInRequest.email)) {
            return userRepository.findByEmail(userSignInRequest.email).get().toMono().flatMap { user ->
                if (user.password == userSignInRequest.password) {
                    val newDeviceList = listOf(
                        DeviceModel(
                            id = userSignInRequest.deviceId,
                            userId = user.userId
                        )
                    )

                    user.devices += newDeviceList

                    userRepository.save(
                        user
                    )
                    Mono.defer {
                        Mono.just(DefaultResponse("Sign In Successfully"))
                    }

                } else {
                    Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_UNAUTHORISED, "Password Incorrect"))
                }
            }

        } else {
            return Mono.error(
                KoshaGatewayException(
                    McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "" +
                            "User with email ${userSignInRequest.email} does not exist"
                )
            )
        }
    }

    fun getUserInfo(userId: String): Mono<GetUserInfoResponse> {
        return userRepository.getReferenceById(userId).toMono()
            .flatMap { user ->
                Mono.just(user.toGetUserInfo())
            }
    }

    fun resendOtp(email: String): Mono<String> {
        val otp = generateSixDigitOTP()
        sendOtp(otp, email)
        return saveOtp(UUID.randomUUID().toString(), otp)
            .flatMap { id ->
                Mono.just(id)
            }
    }

}

