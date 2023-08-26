package com.sage.sage.microservices.user.service

import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.SignInResponse
import com.sage.sage.microservices.user.repository.UserRepository
import com.sage.sage.microservices.user.model.request.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {


    fun create(userRegistrationRequest: UserRegistrationRequest): ResponseEntity<String> {

        val checkUsername = userRepository.getByUsername(userRegistrationRequest.username)
        println("Sageem UserName :  ${checkUsername?.username}")

        return if (checkUsername != null) {
            ResponseEntity("Username ${userRegistrationRequest.username} already exists", HttpStatus.CONFLICT)
        } else {
            val response = userRepository.create(userRegistrationRequest)
            if (response == "Error") {
                ResponseEntity("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR)
            } else {
                ResponseEntity("$response \nUser Registration Success", HttpStatus.CREATED)
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
        val user = userRepository.getByUsername(userSignInRequest.username)
        if (user != null) {
            return if (userSignInRequest.password == user.password) {
                userRepository.createDevice(
                    DeviceModel(
                        deviceId = userSignInRequest.deviceId,
                        isLoggedIn = true,
                        userUsername = userSignInRequest.username,
                    )
                )
                ResponseEntity(SignInResponse( message = "User Successfully Signed In"), HttpStatus.OK)
            } else {
                ResponseEntity(SignInResponse( message = "Password Incorrect"), HttpStatus.CONFLICT)
            }
        } else {
            return  ResponseEntity(SignInResponse( message = "User does not exist"), HttpStatus.NOT_FOUND)
        }
    }

    fun getByUsername(username: String): User? {
        return userRepository.getByUsername(username)
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

