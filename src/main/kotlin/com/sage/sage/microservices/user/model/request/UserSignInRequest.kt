package com.sage.sage.microservices.user.model.request

import com.fasterxml.jackson.annotation.JsonProperty

class UserSignInRequest(
    @JsonProperty("email") val email: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("deviceId") val deviceId: String
)