package com.sage.sage.microservices.user.service

import com.azure.core.exception.AzureException
import com.azure.cosmos.CosmosException
import com.azure.cosmos.implementation.ConflictException
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.SignInResponse
import com.sage.sage.microservices.user.repository.UserRepository
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.DeviceModelV2
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
                ResponseEntity(e.shortMessage, HttpStatus.CONFLICT)
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
                    DeviceModel(
                        deviceId = userSignInRequest.deviceId,
                        isLoggedIn = true,
                        userUsername = userSignInRequest.username
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

    fun updateName(username: String, userUpdateNameRequest: UserUpdateNameRequest): String {
        return userRepository.updateName(username, userUpdateNameRequest);
    }

    fun updateSurname(username: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): String {
        return userRepository.updateSurname(username, userUpdateSurnameRequest)
    }

    fun updateEmail(username: String, userUpdateEmailRequest: UserUpdateEmailRequest): String {
        return userRepository.updateEmail(username, userUpdateEmailRequest)
    }

    fun updatePassword(username: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): String {
        return userRepository.updatePassword(username, userUpdatePasswordRequest)
    }

    fun deleteByUsername(username: String): String {
        return userRepository.deleteByUsername(username)
    }

}

