package com.sage.sage.microservices.user.model.request

import com.azure.cosmos.models.PartitionKey
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.google.cloud.firestore.annotation.DocumentId
import com.sage.sage.microservices.user.model.response.DeviceModelV2
import com.sage.sage.microservices.user.model.response.DeviceRequest

class UserRegistrationRequest(
    @DocumentId val username: String,
    val name: String,
    val surname: String,
    val password: String,
    val email: String,
    val cellNumber: String,
    val devices: List<DeviceModel>
)

class UserModel(
    val id: String,
    var userKey: String?,
    val name: String,
    val surname: String,
    val password: String,
    val email: String,
    val cellNumber: String,
    val devices: List<DeviceModel>,
    var isVerified: Boolean?,
    var otp: String?
){
    companion object {
        fun UserRegistrationRequestV2.toUserModel(): UserModel {
            val deviceModel = listOf( DeviceModel(deviceId = devices[0].deviceId, isLoggedIn = false, userUsername = id))

            return UserModel(
                id ,
                userKey,
                name,
                surname,
                password,
                email,
                cellNumber,
                deviceModel,
                isVerified,
                otp
            )
        }
    }
}

class UserRegistrationRequestV2(
    val id: String,
    var userKey: String?,
    val name: String,
    val surname: String,
    val password: String,
    val email: String,
    val cellNumber: String,
    val devices: List<DeviceRequest>,
    var isVerified: Boolean?,
    var otp: String?
)