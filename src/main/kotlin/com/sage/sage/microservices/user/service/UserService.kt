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
            if (e.statusCode == HttpStatus.CONFLICT.value()) {
                ResponseEntity(
                    "User with the username ${userRegistrationRequest.id} already exists",
                    HttpStatus.CONFLICT
                )
            } else {
                ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
            }
        }
    }

    fun otpVerification(username: String, request: UserVerificationRequest): ResponseEntity<String> {
        val result = userRepository.otpVerification(username, request)
        return if (result) {
            ResponseEntity("User Verified", HttpStatus.OK)
        } else {
            ResponseEntity("OTP does not match ${request.otp}", HttpStatus.NOT_ACCEPTABLE)
        }
    }

    fun resendOtp(username: String): ResponseEntity<String> {
        return try {
            val user = userRepository.getByUsername(username)
            val otp = userRepository.resendOtp(email = user?.email.toString())
            userRepository.updateOtp(username, otp)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun signUserIn(userSignInRequest: UserSignInRequest): ResponseEntity<SignInResponse> {
        return try {
            val user = userRepository.getByUsername(userSignInRequest.username)
            if (user?.password == userSignInRequest.password) {
                userRepository.createDevice(
                    DeviceModelV2(
                        id = userSignInRequest.deviceId,
                        userKey = "device",
                        isLoggedIn = true,
                        userUsername = userSignInRequest.username
                    )
                )
                userRepository.addDevice(
                    userSignInRequest.username,
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
            if (e.statusCode == HttpStatus.NOT_FOUND.value()) {
                ResponseEntity(
                    SignInResponse(message = "User with username ${userSignInRequest.username} does not exist"),
                    HttpStatus.NOT_FOUND
                )
            } else {
                ResponseEntity(SignInResponse(message = e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
            }
        }
    }

    fun updateName(username: String, userUpdateNameRequest: UserUpdateNameRequest): ResponseEntity<String> {
        return try {
            userRepository.updateName(username, userUpdateNameRequest)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }

    }

    fun updateSurname(username: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): ResponseEntity<String> {
        return try {
            userRepository.updateSurname(username, userUpdateSurnameRequest)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun updateEmail(username: String, userUpdateEmailRequest: UserUpdateEmailRequest): ResponseEntity<String> {
        return try {
            userRepository.updateEmail(username, userUpdateEmailRequest)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun updatePassword(username: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): ResponseEntity<String> {
        return try {
            userRepository.updatePassword(username, userUpdatePasswordRequest)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun deleteByUsername(username: String): ResponseEntity<String> {
        return try {
            userRepository.deleteByUsername(username)
            ResponseEntity(HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }
    }

}

