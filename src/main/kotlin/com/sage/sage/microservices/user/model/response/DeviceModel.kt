package com.sage.sage.microservices.user.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.cloud.firestore.annotation.DocumentId

class DeviceModel (
    @JsonProperty("deviceId") val deviceId: String,
    @JsonProperty("loggedIn") val isLoggedIn: Boolean,
    @JsonProperty("userUsername") val userUsername: String
)

class DeviceModelV2 (
    @JsonProperty("id") val id: String,
    @JsonProperty("userKey") val userKey: String?,
    @JsonProperty("loggedIn") val isLoggedIn: Boolean,
    @JsonProperty("userUsername") val userUsername: String
)