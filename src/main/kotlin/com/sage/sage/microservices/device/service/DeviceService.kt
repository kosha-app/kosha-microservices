package com.sage.sage.microservices.device.service

import com.sage.sage.microservices.device.repository.IDeviceRepository
import com.sage.sage.microservices.exception.KoshaGatewayException
import com.sage.sage.microservices.exception.McaHttpResponseCode
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.GetDeviceResponse
import com.sage.sage.microservices.user.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class DeviceService(
    private val repository: IDeviceRepository,
    private val userRepository: UserRepository
) {

    fun checkDevice(deviceId: String): Mono<Void>{
        return repository.getDevice(deviceId)
            .switchIfEmpty( Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "")) )
            .flatMap {
                Mono.empty()
            }
    }

    fun getDevice(deviceId: String): Mono<GetDeviceResponse>{
            return repository.getDevice(deviceId)
                .map { device -> GetDeviceResponse(device.userId) }
    }

    fun logDeviceOut(deviceId: String): Mono<Void>{
       return repository.getDevice(deviceId)
            .flatMap { device ->
                userRepository.getProfileByUserId(device?.userId.toString())
                    .flatMap { user ->
                        val currentDevices = user.devices as ArrayList
                        currentDevices.removeIf { device -> device.deviceId == deviceId }

                        repository.deleteDevice(deviceId)
                        repository.amendUserDevices(user.id, currentDevices as List<DeviceModel>)
                    }
            }

    }
}