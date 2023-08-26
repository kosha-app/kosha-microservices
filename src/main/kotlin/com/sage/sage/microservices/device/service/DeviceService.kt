package com.sage.sage.microservices.device.service

import com.sage.sage.microservices.device.model.response.CheckDeviceResponse
import com.sage.sage.microservices.device.repository.IDeviceRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
class DeviceService(
    private val repository: IDeviceRepository
) {

    fun checkDevice(deviceId: String): ResponseEntity<CheckDeviceResponse> {
        val result = repository.checkDevice(deviceId)
        return if (result != null){
            ResponseEntity(result, HttpStatus.OK)
        }else {
            ResponseEntity(CheckDeviceResponse(message = "Device Not Registered To User"), HttpStatus.CONFLICT)
        }
    }
}