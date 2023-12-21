package com.sage.sage.microservices.user.repository

import com.sage.sage.microservices.user.model.OTPModel
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceRequest
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {

    fun create(userRegistrationRequest: UserRegistrationRequestV2): Int?

    fun sendOtp(id: String, email: String): String

    fun updateOtp(id: String, otp: String): String

    fun registrationCancelled(email: String)

    fun otpVerification(id: String, request: UserVerificationRequest): Boolean

    fun createDevice(deviceModel: DeviceModel): String

    fun addDevice(email: String, deviceModel: DeviceRequest): Int?

    fun getProfileByUserId(userId: String): User?

    fun getOtpById(id: String): OTPModel?

    fun getByEmail(email: String): User?

    fun deleteByEmail(email: String): Int?

    fun updatePassword(email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): String

    fun updateEmail(email: String, userUpdateEmailRequest: UserUpdateEmailRequest): String

    fun updateSurname(email: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): String

    fun updateName(email: String, userUpdateNameRequest: UserUpdateNameRequest): String
}