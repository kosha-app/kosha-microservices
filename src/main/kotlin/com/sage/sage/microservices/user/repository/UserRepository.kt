package com.sage.sage.microservices.user.repository

import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.UserV2
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.DeviceModelV2
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {

    fun create(userRegistrationRequest: UserRegistrationRequestV2): Pair<Int?, String?>

    fun sendOtp(email: String, otp: String)

    fun otpVerification(username: String, request: UserVerificationRequest): Boolean

    fun createDevice(deviceModel: DeviceModelV2): String

    fun addDevice(username: String, deviceModel: DeviceModel): Int?

    fun getByUsername(username: String): UserV2?

    fun deleteByUsername(username: String): String

    fun updatePassword(username: String, userUpdateNameRequest: UserUpdatePasswordRequest): String

    fun updateEmail(username: String, userUpdateNameRequest: UserUpdateEmailRequest): String

    fun updateSurname(username: String, userUpdateNameRequest: UserUpdateSurnameRequest): String

    fun updateName(username: String, userUpdateNameRequest: UserUpdateNameRequest): String
}