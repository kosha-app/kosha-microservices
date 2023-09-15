//package com.sage.sage.microservices.device.service
//
//import com.azure.cosmos.CosmosException
//import com.sage.sage.microservices.azure.AzureInitializer
//import com.sage.sage.microservices.device.model.response.CheckDeviceResponse
//import com.sage.sage.microservices.device.repository.DeviceRepository
//import com.sage.sage.microservices.device.repository.IDeviceRepository
//import com.sage.sage.microservices.user.model.response.DeviceModel
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.InjectMocks
//import org.mockito.Mock
//import org.mockito.Mockito.`when`
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//
//class DeviceServiceTest {
//
//    @Mock
//    private lateinit var repository: IDeviceRepository
//
//    @Mock
//    private lateinit var azureInitializer: AzureInitializer
//
//    @InjectMocks
//    private lateinit var service: DeviceService
//
//    @BeforeEach
//    fun setUp() {
//        repository = DeviceRepository(azureInitializer)
//        `when`(repository.getDevice("device1")).thenReturn(DeviceModel("id", userKey = "Key", userId = "userId"))
//        `when`(repository.getDevice("device2")).thenThrow(CosmosException(404, "Device not found", null, null))
//        `when`(repository.getDevice("device3")).thenThrow(CosmosException(500, "Internal server error", null, null))
//        CosmosException()
//    }
//
//    @Test
//    fun testCheckDeviceWhenDeviceFoundThenReturnOk() {
//        // Arrange
//        val expectedResponse = ResponseEntity(CheckDeviceResponse(true, "Device Logged in with user"), HttpStatus.OK)
//
//        // Act
//        val actualResponse = service.checkDevice("device1")
//
//        // Assert
//        assertEquals(expectedResponse, actualResponse)
//    }
//
//    @Test
//    fun testCheckDeviceWhenDeviceNotFoundThenReturnNotFound() {
//        // Arrange
//        val expectedResponse = ResponseEntity(CheckDeviceResponse(false, "Device not logged in"), HttpStatus.NOT_FOUND)
//
//        // Act
//        val actualResponse = service.checkDevice("device2")
//
//        // Assert
//        assertEquals(expectedResponse, actualResponse)
//    }
//
//    @Test
//    fun testCheckDeviceWhenRepositoryThrowsExceptionThenReturnExceptionStatus() {
//        // Arrange
//        val expectedResponse =
//            ResponseEntity(CheckDeviceResponse(null, "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR)
//
//        // Act
//        val actualResponse = service.checkDevice("device3")
//
//        // Assert
//        assertEquals(expectedResponse, actualResponse)
//    }
//}