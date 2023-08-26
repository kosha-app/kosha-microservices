package com.sage.sage.microservices.user.repository

import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.request.*
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {

    fun create(userRegistrationRequest: UserRegistrationRequest): String

    fun createDevice(deviceModel: DeviceModel): String

    fun getByUsername(username: String): User?

    fun deleteByUsername(username: String): String

    fun otpVerification(username: String,request: UserVerificationRequest): Boolean

    fun updatePassword(username: String, userUpdateNameRequest: UserUpdatePasswordRequest): String

    fun updateEmail(username: String, userUpdateNameRequest: UserUpdateEmailRequest): String

    fun updateSurname(username: String, userUpdateNameRequest: UserUpdateSurnameRequest): String

    fun updateName(username: String, userUpdateNameRequest: UserUpdateNameRequest): String
}