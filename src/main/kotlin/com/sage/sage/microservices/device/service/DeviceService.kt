package com.sage.sage.microservices.device.service

import com.azure.cosmos.CosmosException
import com.sage.sage.microservices.device.model.response.CheckDeviceResponse
import com.sage.sage.microservices.device.repository.IDeviceRepository
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
class DeviceService(
    private val repository: IDeviceRepository
) {

    fun checkDevice(deviceId: String): ResponseEntity<String> {
        return try {
            repository.checkDevice(deviceId)
            ResponseEntity(
                "Device Logged in with user",
                HttpStatus.OK
            )
        } catch (e: CosmosException) {
            ResponseEntity( e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }

    }
}