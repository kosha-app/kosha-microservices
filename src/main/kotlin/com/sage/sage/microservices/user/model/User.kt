package com.sage.sage.microservices.user.model

import com.azure.cosmos.models.PartitionKey
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.annotation.DocumentId
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceModelV2

class User(
    @DocumentId
    val username: String,
    val name: String?,
    val surname: String?,
    val password: String?,
    val email: String?,
    val cellNumber: String?,
    val isVerified: Boolean?,
    val otp: String?
) {
    companion object {
        fun DocumentSnapshot.toUser(): User {
            return User(
                username = id,
                name = getString("name"),
                surname = getString("surname"),
                email = getString("email"),
                password = getString("password"),
                cellNumber = getString("cellNumber"),
                isVerified = getBoolean("isVerified"),
                otp = getString("otp")
            )
        }
    }
}



class UserV2(
    @JsonProperty("id") val id: String,
    @JsonProperty("partitionKey") val partitionKey: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("surname") val surname: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("cellNumber") val cellNumber: String,
    @JsonProperty("devices") val devices: List<DeviceModelV2>
//    val isVerified: Boolean?,
//    val otp: String?
)


