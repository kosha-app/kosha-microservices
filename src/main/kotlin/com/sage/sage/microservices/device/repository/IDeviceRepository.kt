package com.sage.sage.microservices.device.repository

import com.sage.sage.microservices.device.model.request.UpdateLogInRequest
import com.sage.sage.microservices.device.model.response.CheckDeviceResponse
import com.sage.sage.microservices.user.model.response.DeviceModel
import org.springframework.stereotype.Repository

@Repository
interface IDeviceRepository {

    fun getDevice(deviceId: String): DeviceModel?

    fun deleteDevice(deviceId: String)

    fun deleteDeviceFromUser(userId: String, devices: List<DeviceModel>)
}