package com.sage.sage.microservices.device.service

import com.azure.cosmos.CosmosException
import com.sage.sage.microservices.device.model.response.CheckDeviceResponse
import com.sage.sage.microservices.device.repository.IDeviceRepository
import com.sage.sage.microservices.user.model.response.DefaultResponse
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.GetDeviceResponse
import com.sage.sage.microservices.user.model.response.GetDeviceResponse.Companion.toResponse
import com.sage.sage.microservices.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
class DeviceService(
    private val repository: IDeviceRepository,
    private val userRepository: UserRepository
) {

    fun checkDevice(deviceId: String): ResponseEntity<CheckDeviceResponse> {
        return try {
            val device = repository.getDevice(deviceId)
            if(device == null){
                ResponseEntity(
                    CheckDeviceResponse(false, "Device not logged in"),
                    HttpStatus.NOT_FOUND
                )
            }else{
                ResponseEntity(
                    CheckDeviceResponse(true, "Device Logged in with user"),
                    HttpStatus.OK
                )
            }
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

    fun getDevice(deviceId: String): ResponseEntity<GetDeviceResponse>{
        return try {
            val response = repository.getDevice(deviceId)
            ResponseEntity(response?.toResponse(), HttpStatus.OK)
        }catch (e: CosmosException){
            ResponseEntity(GetDeviceResponse(message = e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
        }
    }

//    fun logDeviceOut(deviceId: String): ResponseEntity<DefaultResponse>{
//       return try {
//            val device =  repository.getDevice(deviceId)
//            val userEmail = userRepository.getProfileByUserId(device?.userId.toString())?.email
//            val user = userRepository.getByEmail(userEmail.toString())
//
//            val currentDevices = user?.devices as ArrayList
//            currentDevices.removeIf {
//                 it.deviceId == deviceId
//            }
//
//            repository.deleteDevice(deviceId)
//            device?.userId?.let { repository.deleteDeviceFromUser(it, currentDevices as List<DeviceModel>) }
//           ResponseEntity(DefaultResponse(message = "Device logged out"), HttpStatus.OK)
//        } catch (e: CosmosException){
//            ResponseEntity(DefaultResponse(message = e.shortMessage), HttpStatusCode.valueOf(e.statusCode))
//        }
//    }
}