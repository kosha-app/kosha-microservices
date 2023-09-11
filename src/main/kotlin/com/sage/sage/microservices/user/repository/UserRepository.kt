package com.sage.sage.microservices.user.repository

import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.DeviceModelV2
import com.sage.sage.microservices.user.model.response.DeviceRequest
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {

    fun create(userRegistrationRequest: UserRegistrationRequestV2): Pair<Int?, String?>

    fun checkEmail(email: String): Int?

    fun sendOtp(email: String, otp: String)

    fun resendOtp(email: String): String

    fun updateOtp(email: String, otp: String): String

    fun otpVerification(email: String, request: UserVerificationRequest): Boolean

    fun createDevice(deviceModel: DeviceModelV2): String

    fun addDevice(email: String, deviceModel: DeviceRequest): Int?

    fun getByEmail(email: String): User?

    fun deleteByEmail(email: String): Int?

    fun updatePassword(email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): String

    fun updateEmail(email: String, userUpdateEmailRequest: UserUpdateEmailRequest): String

    fun updateSurname(email: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): String

    fun updateName(email: String, userUpdateNameRequest: UserUpdateNameRequest): String
}