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
        return try {
            repository.getDevice(deviceId)
                ResponseEntity(
                    CheckDeviceResponse(true, "Device Logged in with user"),
                    HttpStatus.OK
                )
        } catch (e: CosmosException) {
            if (e.statusCode == 404){
                ResponseEntity(
                    CheckDeviceResponse(false, "Device not logged in"),
                    HttpStatus.NOT_FOUND
                )
            }else {
                ResponseEntity(CheckDeviceResponse(null, e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
            }
        }

    }
}