package com.sage.sage.microservices.device.repository

import com.sage.sage.microservices.device.model.request.UpdateLogInRequest
import com.sage.sage.microservices.device.model.response.CheckDeviceResponse
import com.sage.sage.microservices.user.model.response.DeviceModelV2
import org.springframework.stereotype.Repository

@Repository
interface IDeviceRepository {

    fun checkDevice(deviceId: String): DeviceModelV2?
}