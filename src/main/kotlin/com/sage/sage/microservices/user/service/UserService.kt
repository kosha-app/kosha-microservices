package com.sage.sage.microservices.user.service

import com.azure.cosmos.CosmosException
import com.sage.sage.microservices.user.model.response.SignInResponse
import com.sage.sage.microservices.user.repository.UserRepository
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.DeviceModelV2
import com.sage.sage.microservices.user.model.response.DeviceRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun create(userRegistrationRequest: UserRegistrationRequestV2): ResponseEntity<String> {
        return try {
            val response = userRepository.create(userRegistrationRequest)
            println("Sage Create User V2 Response Code: ${response.first}")
            userRepository.sendOtp(userRegistrationRequest.email, response.second.toString())
            ResponseEntity("User Successfully created", HttpStatus.CREATED)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))

        }
    }

    fun checkEmail(email: String): ResponseEntity<String> {
        return try {
            val response = userRepository.getByEmail(email)
            if (response == null){
                ResponseEntity("User with email $email already exists",HttpStatus.CONFLICT)

            } else {
                ResponseEntity(HttpStatus.OK)
            }
        } catch (e: CosmosException){
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun otpVerification(email: String, request: UserVerificationRequest): ResponseEntity<String> {
        val result = userRepository.otpVerification(email, request)
        return if (result) {
            ResponseEntity("User Verified", HttpStatus.OK)
        } else {
            ResponseEntity("OTP does not match ${request.otp}", HttpStatus.NOT_ACCEPTABLE)
        }
    }

    fun resendOtp(email: String): ResponseEntity<String> {
        return try {
            val user = userRepository.getByEmail(email)
            val otp = userRepository.resendOtp(email = user?.email.toString())
            userRepository.updateOtp(email, otp)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun signUserIn(userSignInRequest: UserSignInRequest): ResponseEntity<SignInResponse> {
        return try {
            val user = userRepository.getByEmail(userSignInRequest.email)
            if (user?.password == userSignInRequest.password) {
                userRepository.createDevice(
                    DeviceModelV2(
                        id = userSignInRequest.deviceId,
                        userKey = "device",
                        isLoggedIn = true,
                        userUsername = userSignInRequest.email
                    )
                )
                userRepository.addDevice(
                    userSignInRequest.email,
                    DeviceRequest(
                        deviceId = userSignInRequest.deviceId,
                        isLoggedIn = true
                    )
                )
                ResponseEntity(SignInResponse(message = "User Successfully Signed In"), HttpStatus.OK)
            } else {
                ResponseEntity(SignInResponse(message = "Password Incorrect"), HttpStatus.CONFLICT)
            }
        } catch (e: CosmosException) {
            ResponseEntity(SignInResponse(message = e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun updateName(email: String, userUpdateNameRequest: UserUpdateNameRequest): ResponseEntity<String> {
        return try {
            userRepository.updateName(email, userUpdateNameRequest)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }

    }

    fun updateSurname(email: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): ResponseEntity<String> {
        return try {
            userRepository.updateSurname(email, userUpdateSurnameRequest)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun updateEmail(email: String, userUpdateEmailRequest: UserUpdateEmailRequest): ResponseEntity<String> {
        return try {
            userRepository.updateEmail(email, userUpdateEmailRequest)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun updatePassword(email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): ResponseEntity<String> {
        return try {
            userRepository.updatePassword(email, userUpdatePasswordRequest)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun deleteByEmail(email: String): ResponseEntity<String> {
        return try {
            userRepository.deleteByEmail(email)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

}

