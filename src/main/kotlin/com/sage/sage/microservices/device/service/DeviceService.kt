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

    fun checkDevice(deviceId: String): ResponseEntity<CheckDeviceResponse> {
        val result = repository.checkDevice(deviceId)
        return if (result != null) {
            ResponseEntity(result, HttpStatus.OK)
        } else {
            ResponseEntity(CheckDeviceResponse(message = "Device Not Registered To User"), HttpStatus.CONFLICT)
        }
    }

    fun checkDeviceV2(deviceId: String): ResponseEntity<CheckDeviceResponse> {
        return try {
            val result = repository.checkDeviceV2(deviceId)
            ResponseEntity(
                CheckDeviceResponse(
                    message = "Device Logged in with user",
                    loggedIn = true,
                    userUsername = result?.userUsername
                ), HttpStatus.OK
            )
        } catch (e: CosmosException) {
            ResponseEntity(CheckDeviceResponse(message = e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }

    }
}