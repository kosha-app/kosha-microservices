package com.sage.sage.microservices.user.model.request

import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceRequest
import java.util.UUID


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
    var isVerified: Boolean?
)

class UserRegistrationRequestV2(
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
    var isVerified: Boolean?
){
    companion object{
        fun UserRegistrationRequestV2.toUserRegistration(): UserRegistration{
            val registerDevice = listOf(DeviceRequest(deviceId = deviceId))
            val id: String = UUID.randomUUID().toString()
            val userKey = null
            val isVerified = null
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
                isVerified
            )
        }
    }
}