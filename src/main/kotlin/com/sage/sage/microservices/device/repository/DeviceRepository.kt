package com.sage.sage.microservices.device.repository

import com.sage.sage.microservices.user.model.response.DeviceModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : JpaRepository<DeviceModel, String>