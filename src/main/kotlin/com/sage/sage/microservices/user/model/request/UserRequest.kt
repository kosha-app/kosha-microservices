package com.sage.sage.microservices.user.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class UserUpdateNameRequest(
        @JsonProperty("newName") var newName: String
)

data class UserUpdatePasswordRequest(
        @JsonProperty("newPassword") val newPassword: String = ""
)

data class UserUpdateEmailRequest(
        @JsonProperty("newEmail") val newEmail: String = ""
)



class UserVerificationRequest(
        @JsonProperty("otp") val otp: String
)
