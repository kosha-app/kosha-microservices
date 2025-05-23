package com.sage.sage.microservices.device.controller

import com.sage.sage.microservices.device.service.DeviceService
import com.sage.sage.microservices.user.model.response.GetDeviceResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/device")
class DeviceController(
    private val deviceService: DeviceService
) {

    @GetMapping("/checkdevice/{deviceId}")
    fun checkDevice(@PathVariable deviceId: String): Mono<Void> {
        return deviceService.checkDevice(deviceId)
    }

    @GetMapping("/{deviceId}")
    fun getDevice(@PathVariable deviceId: String): Mono<GetDeviceResponse>{
        return deviceService.getDevice(deviceId)
    }

    @PutMapping("/logout/{deviceId}")
    fun logDeviceOut(@PathVariable deviceId: String): Mono<Void> {
        return deviceService.logDeviceOut(deviceId)
    }
}