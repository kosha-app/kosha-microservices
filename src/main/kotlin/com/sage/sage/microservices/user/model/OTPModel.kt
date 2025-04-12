package com.sage.sage.microservices.user.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class OTPModel(
    @Id val id: String,
    val otp: String
)