package com.sage.sage.microservices.user.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.cloud.firestore.annotation.DocumentId

class DeviceModel (
    val deviceId: String,
    val isLoggedIn: Boolean,
    val userUsername: String
)

class DeviceModelV2 (
    @JsonProperty("deviceId") val deviceId: String,
    @JsonProperty("isLoggedIn") val isLoggedIn: Boolean,
    @JsonProperty("userUsername") val userUsername: String
)