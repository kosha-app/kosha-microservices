package com.sage.sage.microservices.user.repository

import com.sage.sage.microservices.user.model.OTPModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OtpRepository : JpaRepository<OTPModel, String>