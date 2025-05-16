//package com.sage.sage.microservices.device.service
//
//import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
//import com.sage.sage.microservices.user.model.User
//import com.sage.sage.microservices.user.model.response.DeviceModel
//import com.sage.sage.microservices.user.model.response.DeviceRequest
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito.*
//import reactor.core.publisher.Mono
//
//import reactor.test.StepVerifier
//import kotlin.collections.ArrayList
//
//class DeviceServiceTest {
//
//    // Mocking the dependencies
//    private val repository: IDeviceRepository = mock(IDeviceRepository::class.java)
//    private val userRepository: UserRepository = mock(UserRepository::class.java)
//
//    // Creating the instance of DeviceService with mocked dependencies
//    private val deviceService = DeviceService(repository, userRepository)
//
//    @Test
//    fun `checkDevice should return Mono error for non-existent device`() {
//        // Arrange
//        val deviceId = "device-id"
//
//        // Mocking the behavior of getDevice to return an empty Mono
//        `when`(repository.getDevice(deviceId)).thenReturn(Mono.empty())
//
//        // Act
//        val result = deviceService.checkDevice(deviceId)
//
//        // Assert
//        StepVerifier.create(result)
//            .expectError(KoshaGatewayException::class.java)
//            .verify()
//
//    }
//
//    @Test
//    fun `checkDevice should return Mono empty for existing device`() {
//        // Arrange
//        val deviceId = "device-id"
//        val existingDevice = DeviceModel(deviceId, "userKey", "userId")
//
//        // Mocking the behavior of getDevice to return a Mono with an existing device
//        `when`(repository.getDevice(deviceId)).thenReturn(Mono.just(existingDevice))
//
//        // Act
//        val result = deviceService.checkDevice(deviceId)
//
//        // Assert
//        StepVerifier.create(result)
//            .expectComplete()
//            .verify()
//    }
//
//    @Test
//    fun `getDevice should return Mono GetDeviceResponse`() {
//        // Arrange
//        val deviceId = "device-id"
//        val userId = "userId"
//        val existingDevice = DeviceModel(deviceId, "userKey", userId)
//
//        // Mocking the behavior of getDevice to return a Mono with an existing device
//        `when`(repository.getDevice(deviceId)).thenReturn(Mono.just(existingDevice))
//
//        // Act
//        val result = deviceService.getDevice(deviceId)
//
//        // Assert
//        StepVerifier.create(result)
//            .assertNext {
//                assertEquals(it.userId, userId)
//            }
//            .verifyComplete()
//    }
//
//    @Test
//    fun `getDevice should throws NotFoundException`() {
//        // Arrange
//        val deviceId = "device-id"
//
//        // Mocking the behavior of getDevice to return a Mono with an existing device
//        `when`(repository.getDevice(deviceId)).thenReturn(Mono.empty())
//
//        // Act
//        val result = deviceService.getDevice(deviceId)
//
//        // Assert
//        StepVerifier.create(result)
//            .expectError(KoshaGatewayException::class.java)
//            .verify()
//    }
//
//    @Test
//    fun `logDeviceOut should remove device, delete from repository, and amend user devices`() {
//        // Arrange
//        val deviceId = "existing-device-id"
//        val userId = "user-id"
//        val devices = ArrayList<DeviceRequest>()
//        devices.add(DeviceRequest(deviceId))
//        devices.add(DeviceRequest("deviceId2"))
//        devices.add(DeviceRequest("deviceId3"))
//        val existingDevice = DeviceModel(deviceId,"", userId)
//        val user = User(userId, "", "", "", "", "", "", "", devices, false )
//
//        `when`(repository.getDevice(deviceId)).thenReturn(Mono.just(existingDevice))
//        `when`(userRepository.getProfileByUserId(userId)).thenReturn(Mono.just(user))
//        `when`(repository.deleteDevice(deviceId)).thenReturn(Mono.empty())
//        `when`(repository.amendUserDevices(userId, devices as List<DeviceModel>)).thenReturn(Mono.empty())
//
//        // Act
//        val result = deviceService.logDeviceOut(deviceId)
//
//        // Assert
//        StepVerifier.create(result)
//            .expectComplete()
//            .verify()
//
//        verify(repository).amendUserDevices(userId, devices as List<DeviceModel>)
//        verify(repository).deleteDevice(deviceId)
//    }
//
//}