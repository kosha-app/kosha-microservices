package com.sage.sage.microservices.user.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.user.model.response.DeviceRequest

class User(
    @JsonProperty("id") val id: String,
    @JsonProperty("userKey") val partitionKey: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("dateOfBirth") val dateOfBirth: String,
    @JsonProperty("gender") val gender: String,
    @JsonProperty("cellNumber") val cellNumber: String?,
    @JsonProperty("devices") val devices: List<DeviceRequest>,
    @JsonProperty("isVerified") val isVerified: Boolean?,
    @JsonProperty("otp") val otp: String?
)


