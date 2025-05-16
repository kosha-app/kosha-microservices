package com.sage.sage.microservices.user.service

import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import com.sage.sage.microservices.exception.exceptionobjects.McaHttpResponseCode
import com.sage.sage.microservices.featuretoggles.KoshaProdFeatureToggles
import com.sage.sage.microservices.user.model.OTPModel
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.User.Companion.toMono
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.*
import com.sage.sage.microservices.user.model.response.GetUserInfoResponse.Companion.toGetUserInfo
import com.sage.sage.microservices.user.repository.EmailTemplateConstants
import com.sage.sage.microservices.user.repository.OtpRepository
import com.sage.sage.microservices.user.repository.UserRepository
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID
import kotlin.random.Random

@Service
class UserService(
    private val userRepository: UserRepository,
    private val otpRepository: OtpRepository,
    private val emailSender: JavaMailSender,
    private val featureToggles: KoshaProdFeatureToggles
) {

    /**
     * Registers a new user with the provided information and device details.
     *
     * Creates a user record with a unique identifier and saves it to the repository.
     * The user's device information is also associated during registration.
     *
     * @param request The registration details for the new user.
     * @return A Mono that completes when the user has been successfully created.
     */
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


    /**
     * Checks if the provided email is already registered and, if not, generates and sends a one-time password (OTP) to the email address.
     *
     * If the email is already associated with an existing user, returns an error. Otherwise, generates a six-digit OTP, sends it to the specified email, saves the OTP, and returns a response containing the OTP record ID.
     *
     * @param email The email address to check and send the OTP to.
     * @return A Mono emitting a response with the OTP record ID if the email is not registered.
     */
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

    /**
     * Sends a verification email containing the provided OTP to the specified email address.
     *
     * @param otp The one-time password to include in the email.
     * @param email The recipient's email address.
     */
    fun sendOtp(otp: String, email: String) {
        val message = SimpleMailMessage()
        message.from = "noreply@kosha.com"
        message.setTo(email)
        message.subject = EmailTemplateConstants.VERIFICATION_EMAIL_SUBJECT
        message.text = EmailTemplateConstants.VERIFICATION_EMAIL_BODY.format(otp)
        emailSender.send(message)
    }

    /**
     * Generates a random 6-digit numeric one-time password (OTP).
     *
     * @return A string containing a randomly generated 6-digit OTP.
     */
    private fun generateSixDigitOTP(): String {
        val otpLength = 6
        val otpBuilder = StringBuilder()

        repeat(otpLength) {
            val randomDigit = Random.nextInt(0, 10)
            otpBuilder.append(randomDigit)
        }

        return otpBuilder.toString()
    }

    /**
     * Saves an OTP with the specified ID and value, and returns the OTP record's ID.
     *
     * @param id The unique identifier for the OTP record.
     * @param otp The one-time password to be saved.
     * @return A Mono emitting the ID of the saved OTP record.
     */
    private fun saveOtp(id: String, otp: String): Mono<String> {
        val otpModel = otpRepository.save(OTPModel(id, otp))
        return Mono.just(otpModel.id)
    }

    /**
     * Verifies a user's OTP using the provided OTP record ID and verification request.
     *
     * Returns an empty Mono if verification succeeds; otherwise, emits an error if the OTP does not match.
     */
    fun otpVerification(id: String, request: UserVerificationRequest): Mono<Void> {
        return verifyOtp(id, request).flatMap { isVerified ->
            if (isVerified) {
                Mono.empty()
            } else {
                Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_UNAUTHORISED, "OTP does not match"))
            }
        }
    }

    /**
     * Verifies whether the provided OTP matches the stored OTP for the given ID.
     *
     * If the OTP bypass feature is enabled, verification also succeeds if the provided OTP is "123456".
     *
     * @param id The identifier of the OTP record.
     * @param request The verification request containing the OTP to check.
     * @return A Mono emitting true if the OTP is valid, false otherwise.
     */
    private fun verifyOtp(id: String, request: UserVerificationRequest): Mono<Boolean> {
        val otp = otpRepository.getReferenceById(id)

        return if (featureToggles.otpBypass) {
            Mono.just(request.otp == "123456" || otp.otp == request.otp)
        } else {
            Mono.just(otp.otp == request.otp)
        }
    }

    /**
     * Authenticates a user by email and password, adds a new device to the user's device list, and returns a success response.
     *
     * Returns an error if the user does not exist or if the password is incorrect.
     *
     * @param userSignInRequest The sign-in request containing user credentials and device information.
     * @return A Mono emitting a success response if authentication succeeds, or an error if authentication fails.
     */
    fun signUserIn(userSignInRequest: UserSignInRequest): Mono<DefaultResponse> {
        if (userRepository.existsByEmail(userSignInRequest.email)) {
            return userRepository.findByEmail(userSignInRequest.email).get().toMono().flatMap { user ->
                if (user.password == userSignInRequest.password) {
                    val newDeviceList = listOf(DeviceModel(
                        id = userSignInRequest.deviceId,
                        userId = user.userId
                    ))

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

    /**
     * Retrieves user information for the specified user ID.
     *
     * @param userId The unique identifier of the user.
     * @return A Mono emitting the user's information as a GetUserInfoResponse.
     */
    fun getUserInfo(userId: String): Mono<GetUserInfoResponse> {
        return userRepository.getReferenceById(userId).toMono()
            .flatMap { user ->
                Mono.just(user.toGetUserInfo())
            }
    }

    /**
     * Generates a new six-digit OTP, sends it to the specified email address, saves the OTP, and returns its record ID.
     *
     * @param email The recipient's email address.
     * @return A Mono emitting the ID of the saved OTP record.
     */
    fun resendOtp(email: String): Mono<String> {
        val otp = generateSixDigitOTP()
        sendOtp(otp, email)
        return saveOtp(UUID.randomUUID().toString(), otp)
            .flatMap { id ->
                Mono.just(id)
            }
    }

}

