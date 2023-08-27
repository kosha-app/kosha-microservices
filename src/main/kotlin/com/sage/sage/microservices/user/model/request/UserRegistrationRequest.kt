package com.sage.sage.microservices.user.model.request

import com.azure.cosmos.models.PartitionKey
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.google.cloud.firestore.annotation.DocumentId

class UserRegistrationRequest(
    @DocumentId val username: String,
    val name: String,
    val surname: String,
    val password: String,
    val email: String,
    val cellNumber: String,
    val devices: List<DeviceModel>
)

class UserRegistrationRequestV2(
    val id: String,
    val partitionKey: String,
    val name: String,
    val surname: String,
    val password: String,
    val email: String,
    val cellNumber: String,
    val devices: List<DeviceModel>,
    var isVerified: Boolean?,
    var otp: String?
)