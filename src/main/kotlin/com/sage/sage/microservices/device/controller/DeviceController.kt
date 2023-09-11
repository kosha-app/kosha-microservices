package com.sage.sage.microservices.device.controller

import com.sage.sage.microservices.device.service.DeviceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/device")
class DeviceController( private val deviceService: DeviceService) {

    @GetMapping("/checkdevice/{deviceId}")
    fun checkDeviceV2(@PathVariable deviceId: String): ResponseEntity<String> {
        return deviceService.checkDevice(deviceId)
    }

}