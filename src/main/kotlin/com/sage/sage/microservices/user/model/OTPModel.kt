package com.sage.sage.microservices.user.model

import com.fasterxml.jackson.annotation.JsonProperty

class OTPModel(
    @JsonProperty("id")val id: String,
    @JsonProperty("userKey")val userKey: String,
    @JsonProperty("email")val email: String,
    @JsonProperty("otp")val otp: String
)