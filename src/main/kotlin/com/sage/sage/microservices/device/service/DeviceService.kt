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

    /**
     * Verifies whether a device with the specified ID exists in the repository.
     *
     * Returns an empty Mono if the device exists. If not, returns a Mono error with a
     * KoshaGatewayException indicating the device is not linked to a profile.
     *
     * @param deviceId The unique identifier of the device to check.
     * @return An empty Mono if the device exists, or a Mono error if not found.
     */
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

    /**
     * Retrieves the user ID associated with the specified device.
     *
     * If the device exists, returns a Mono emitting a GetDeviceResponse containing the user ID.
     * If the device does not exist, returns a Mono error with a KoshaGatewayException.
     *
     * @param deviceId The unique identifier of the device to retrieve.
     * @return A Mono emitting the device's user ID or an error if the device is not found.
     */
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

    /**
     * Removes the device with the specified ID from the repository.
     *
     * @param deviceId The unique identifier of the device to be deleted.
     * @return An empty Mono upon completion.
     */
    fun logDeviceOut(deviceId: String): Mono<Void>{
            deviceRepository.deleteById(deviceId)
            return Mono.empty()
    }
}