package com.sage.sage.microservices.user.service

import com.azure.cosmos.CosmosException
import com.sage.sage.microservices.user.repository.UserRepository
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.*
import com.sage.sage.microservices.user.model.response.GetUserInfoResponse.Companion.toGetUserInfo
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun create(userRegistrationRequest: UserRegistrationRequestV2): ResponseEntity<DefaultResponse> {
        return try {
            userRepository.create(userRegistrationRequest)
            ResponseEntity(DefaultResponse(message = "User Successfully created"), HttpStatus.CREATED)
        } catch (e: CosmosException) {
            ResponseEntity(DefaultResponse(message = e.shortMessage), HttpStatusCode.valueOf(e.statusCode))

        }
    }

    fun checkEmail(email: String): ResponseEntity<CheckEmailResponse> {
        return try {
            val response = userRepository.getByEmail(email)
            if (response != null){
                ResponseEntity(CheckEmailResponse(id = null,message = "User with email $email already exists"),HttpStatus.CONFLICT)
            } else {
                val id = UUID.randomUUID().toString()
                userRepository.sendOtp(id, email)
                ResponseEntity(CheckEmailResponse(id = id, message = "User can register"),HttpStatus.OK)
            }
        } catch (e: CosmosException){
            ResponseEntity(CheckEmailResponse(id = null, e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun otpVerification(id: String, request: UserVerificationRequest): ResponseEntity<DefaultResponse> {
        val result = userRepository.otpVerification(id, request)
        return if (result) {
            ResponseEntity(DefaultResponse("User Verified"), HttpStatus.OK)
        } else {
            ResponseEntity(DefaultResponse("OTP does not match ${request.otp}"), HttpStatus.NOT_ACCEPTABLE)
        }
    }

    fun resendOtp(id: String, email: String): ResponseEntity<DefaultResponse> {
        //TODO still to reimplement resend OTP
        return try {
            val user = userRepository.getByEmail(email)
//            val otp = userRepository.sendOtp(email = user?.email.toString())
//            userRepository.updateOtp(id, otp)
            ResponseEntity(DefaultResponse(""),HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(DefaultResponse(e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun signUserIn(userSignInRequest: UserSignInRequest): ResponseEntity<DefaultResponse> {
        return try {
            val user = userRepository.getByEmail(userSignInRequest.email)
            if (user != null){
                if (user.password == userSignInRequest.password) {
                    userRepository.createDevice(
                        DeviceModel(
                            id = userSignInRequest.deviceId,
                            userKey = "device",
                            userId = user.id
                        )
                    )
                    userRepository.addDevice(
                        userSignInRequest.email,
                        DeviceRequest(
                            deviceId = userSignInRequest.deviceId
                        )
                    )
                    ResponseEntity(DefaultResponse(message = "User Successfully Signed In"), HttpStatus.OK)
                } else {
                    ResponseEntity(DefaultResponse(message = "Password Incorrect"), HttpStatus.CONFLICT)
                }
            }else {
                ResponseEntity(DefaultResponse(message = "User with email ${userSignInRequest.email} does not exist"), HttpStatus.NOT_FOUND)
            }
        } catch (e: CosmosException) {
            ResponseEntity(DefaultResponse(message = e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun getUserInfo(userId: String): ResponseEntity<GetUserInfoResponse> {
        return try {
            val response = userRepository.getProfileByUserId(userId)
            ResponseEntity(response?.toGetUserInfo(), HttpStatus.OK)
        }catch (e: CosmosException){
            ResponseEntity(GetUserInfoResponse(message = e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun updateName(email: String, userUpdateNameRequest: UserUpdateNameRequest): ResponseEntity<DefaultResponse> {
        return try {
            userRepository.updateName(email, userUpdateNameRequest)
            ResponseEntity(DefaultResponse(""),HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(DefaultResponse(e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }

    }

    fun updateSurname(email: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): ResponseEntity<DefaultResponse> {
        return try {
            userRepository.updateSurname(email, userUpdateSurnameRequest)
            ResponseEntity(DefaultResponse(""),HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(DefaultResponse(e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun updateEmail(email: String, userUpdateEmailRequest: UserUpdateEmailRequest): ResponseEntity<DefaultResponse> {
        return try {
            userRepository.updateEmail(email, userUpdateEmailRequest)
            ResponseEntity(DefaultResponse(""),HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(DefaultResponse(e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun updatePassword(email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): ResponseEntity<DefaultResponse> {
        return try {
            userRepository.updatePassword(email, userUpdatePasswordRequest)
            ResponseEntity(DefaultResponse(""),HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(DefaultResponse(e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun deleteByEmail(email: String): ResponseEntity<DefaultResponse> {
        return try {
            userRepository.deleteByEmail(email)
            ResponseEntity(DefaultResponse(""),HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(DefaultResponse(e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

}

