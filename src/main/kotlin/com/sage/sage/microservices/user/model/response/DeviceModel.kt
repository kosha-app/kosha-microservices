package com.sage.sage.microservices.user.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.cloud.firestore.annotation.DocumentId

class DeviceRequest (
    @JsonProperty("deviceId") val deviceId: String
)

class DeviceModel (
    @JsonProperty("id") val id: String,
    @JsonProperty("userKey") val userKey: String?,
    @JsonProperty("userId") val userId: String
)