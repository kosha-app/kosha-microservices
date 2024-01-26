package com.sage.sage.microservices.device.repository

import com.sage.sage.microservices.user.model.response.DeviceModel
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface IDeviceRepository {

    fun getDevice(deviceId: String): Mono<DeviceModel>

    fun deleteDevice(deviceId: String): Mono<Void>

    fun amendUserDevices(userId: String, devices: List<DeviceModel>): Mono<Void>
}