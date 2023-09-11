package com.sage.sage.microservices.user.model.request

import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceRequest



class UserModel(
    val id: String,
    var userKey: String?,
    val name: String,
    val password: String,
    val email: String,
    val dateOfBirth: String,
    val gender: String,
    val cellNumber: String,
    val devices: List<DeviceModel>,
    var isVerified: Boolean?,
    var otp: String?
)

class UserRegistrationRequestV2(
    val id: String,
    val name: String,
    val password: String,
    val email: String,
    val dateOfBirth: String,
    val gender: String,
    val cellNumber: String?,
    val deviceId: String,
)

class UserRegistration(
    val id: String,
    var userKey: String?,
    val name: String,
    val password: String,
    val email: String,
    val dateOfBirth: String,
    val gender: String,
    val cellNumber: String?,
    val devices: List<DeviceRequest>,
    var isVerified: Boolean?,
    var otp: String?
){
    companion object{
        fun UserRegistrationRequestV2.toUserRegistration(): UserRegistration{
            val registerDevice = listOf(DeviceRequest(deviceId = deviceId, isLoggedIn = false))
            val userKey = null
            val isVerified = null
            val otp = null
            return UserRegistration(
                id ,
                userKey,
                name,
                password,
                email,
                dateOfBirth,
                gender,
                cellNumber,
                registerDevice,
                isVerified,
                otp
            )
        }
    }
}