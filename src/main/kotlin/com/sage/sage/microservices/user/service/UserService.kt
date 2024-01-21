package com.sage.sage.microservices.user.service

import com.azure.cosmos.CosmosException
import com.azure.cosmos.implementation.ConflictException
import com.sage.sage.microservices.exception.KoshaGatewayException
import com.sage.sage.microservices.exception.McaHttpResponseCode
import com.sage.sage.microservices.user.repository.UserRepository
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.*
import com.sage.sage.microservices.user.model.response.GetUserInfoResponse.Companion.toGetUserInfo
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.UUID
import javax.swing.text.html.parser.Entity

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun create(userRegistrationRequest: UserRegistrationRequestV2): Mono<Void> {
        return userRepository.create(userRegistrationRequest)
            .flatMap {
                userRepository.createDevice(it)
            }
    }

    fun checkEmail(email: String): Mono<CheckEmailResponse> {
        return userRepository.getByEmail(email)
            .flatMap {
                Mono.error<CheckEmailResponse>(
                    KoshaGatewayException(
                        McaHttpResponseCode.ERROR_ITEM_ALREADY_EXISTS,
                        "User with email $email already exists"
                    )
                )
            }
            .switchIfEmpty(
                userRepository.sendOtp(UUID.randomUUID().toString(), email)
                    .flatMap { id ->
                        Mono.just(CheckEmailResponse(id))
                    }
            )
    }

    fun otpVerification(id: String, request: UserVerificationRequest): Mono<Void> {
       return userRepository.otpVerification(id, request).flatMap { isVerified ->
            if (isVerified){
                Mono.empty()
            } else{
                Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_UNAUTHORISED, "OTP does not match"))
            }
        }
    }

    fun signUserIn(userSignInRequest: UserSignInRequest): Mono<DefaultResponse> {
        return userRepository.getByEmail(userSignInRequest.email).flatMap { user ->
            if (user.password == userSignInRequest.password) {
                userRepository.createDevice(
                    DeviceModel(
                        id = userSignInRequest.deviceId,
                        userKey = "device",
                        userId = user.id
                    )
                ).then(Mono.defer {
                    userRepository.addDevice(
                        email = userSignInRequest.email,
                        deviceModel = DeviceRequest(
                            deviceId = userSignInRequest.deviceId
                        )
                    ).then(
                        Mono.defer {
                            Mono.just(DefaultResponse(""))
                        }
                    )
                })
            } else {
                Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_UNAUTHORISED, "Password Incorrect"))
            }
        }.switchIfEmpty(
            Mono.error(
                KoshaGatewayException(
                    McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "" +
                            "User with email ${userSignInRequest.email} does not exist"
                )
            )
        )
    }

    fun getUserInfo(userId: String): Mono<GetUserInfoResponse> {
        return userRepository.getProfileByUserId(userId)
            .flatMap { user ->
                Mono.just(user.toGetUserInfo())
            }
    }

    fun updateName(email: String, userUpdateNameRequest: UserUpdateNameRequest): Mono<Void> {
        return userRepository.updateName(email, userUpdateNameRequest)
    }

    fun updateSurname(
        email: String,
        userUpdateSurnameRequest: UserUpdateSurnameRequest
    ): Mono<Void> {
        return userRepository.updateSurname(email, userUpdateSurnameRequest)
    }

    fun updateEmail(email: String, userUpdateEmailRequest: UserUpdateEmailRequest): Mono<Void> {
        return userRepository.updateEmail(email, userUpdateEmailRequest)
    }

    fun updatePassword(email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): Mono<Void> {
        return userRepository.updatePassword(email, userUpdatePasswordRequest)
    }

    fun deleteByEmail(email: String): Mono<Void> {
        return  userRepository.deleteByEmail(email)
    }

    fun resendOtp(email: String): Mono<String> {
        val otpId = UUID.randomUUID().toString()
        return userRepository.sendOtp(otpId, email)
    }

}

