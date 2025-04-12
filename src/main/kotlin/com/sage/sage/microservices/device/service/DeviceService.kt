package com.sage.sage.microservices.device.service

import com.sage.sage.microservices.device.repository.DeviceRepository
import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import com.sage.sage.microservices.exception.exceptionobjects.McaHttpResponseCode
import com.sage.sage.microservices.user.model.response.DeviceModel.Companion.toMono
import com.sage.sage.microservices.user.model.response.GetDeviceResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
) {

    fun checkDevice(deviceId: String): Mono<Void>{
       return if (deviceRepository.existsById(deviceId)){
            Mono.empty()
        } else {
            Mono.error(
                KoshaGatewayException(
                    McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION,
                    "Device not linked to profile"
                )
            )
        }
    }

    fun getDevice(deviceId: String): Mono<GetDeviceResponse>{
       return if (deviceRepository.existsById(deviceId)){
           deviceRepository.getReferenceById(deviceId).toMono().map { device ->
               GetDeviceResponse(userId = device.userId)
           }
       } else {
           Mono.error(
               KoshaGatewayException(
                   McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION,
                   "Device does not exist"
               )
           )
       }
    }

    fun logDeviceOut(deviceId: String): Mono<Void>{
            deviceRepository.deleteById(deviceId)
            return Mono.empty()
    }
}