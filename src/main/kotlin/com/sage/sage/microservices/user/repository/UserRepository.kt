package com.sage.sage.microservices.user.repository

import com.sage.sage.microservices.user.model.OTPModel
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceRequest
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository {

    fun create(userRegistrationRequest: UserRegistrationRequestV2): Mono<DeviceModel>

    fun sendOtp(id: String, email: String): Mono<String>

    fun updateOtp(id: String, otp: String): Mono<Void>

    fun registrationCancelled(email: String)

    fun otpVerification(id: String, request: UserVerificationRequest): Mono<Boolean>

    fun createDevice(deviceModel: DeviceModel): Mono<Void>

    fun addDevice(email: String, deviceModel: DeviceRequest): Mono<Void>

    fun getProfileByUserId(userId: String): Mono<User>

    fun getOtpById(id: String): Mono<OTPModel>

    fun getByEmail(email: String): Mono<User>

    fun deleteByEmail(email: String): Mono<Void>

    fun updatePassword(email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): Mono<Void>

    fun updateEmail(email: String, userUpdateEmailRequest: UserUpdateEmailRequest): Mono<Void>

    fun updateName(email: String, userUpdateNameRequest: UserUpdateNameRequest): Mono<Void>
}