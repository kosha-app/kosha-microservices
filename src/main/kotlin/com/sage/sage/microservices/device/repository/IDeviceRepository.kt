package com.sage.sage.microservices.device.repository

import com.sage.sage.microservices.device.model.request.UpdateLogInRequest
import com.sage.sage.microservices.device.model.response.CheckDeviceResponse
import org.springframework.stereotype.Repository

@Repository
interface IDeviceRepository {
    fun checkDevice(deviceId: String): CheckDeviceResponse?

    fun updateDeviceLogIn(deviceId: String, updateLogInRequest: UpdateLogInRequest): String
}