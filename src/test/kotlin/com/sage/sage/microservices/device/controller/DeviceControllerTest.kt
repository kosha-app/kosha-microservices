package com.sage.sage.microservices.device.controller

import com.sage.sage.microservices.device.service.DeviceService
import com.sage.sage.microservices.user.model.response.GetDeviceResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.mockito.Mockito.*
import reactor.core.publisher.Mono


@WebFluxTest
@ActiveProfiles("test")
@ContextConfiguration(classes = [DeviceController::class])
class DeviceControllerTest {


    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var service: DeviceService

    @Test
    fun `test checkdevice endpoint`(){
        val deviceId = "device-id"
        `when`(service.checkDevice(deviceId)).thenReturn(Mono.empty())
        webTestClient
            .get()
            .uri("/device/checkdevice/device-id")
            .exchange()
            .expectStatus()
            .isOk

        verify(service).checkDevice(deviceId)
    }

    @Test
    fun `test getDevice endpoint`(){
        val deviceId = "device-id"
        val getDeviceResponse = GetDeviceResponse("user-id")
        `when`(service.getDevice(deviceId)).thenReturn(Mono.just(getDeviceResponse))
        webTestClient
            .get()
            .uri("/device/device-id")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(GetDeviceResponse::class.java)

        verify(service).getDevice(deviceId)
    }

    @Test
    fun `test logDeviceOut endpoint`(){
        val deviceId = "device-id"
        `when`(service.logDeviceOut(deviceId)).thenReturn(Mono.empty())
        webTestClient
            .put()
            .uri("/device/logout/device-id")
            .exchange()
            .expectStatus()
            .isOk

        verify(service).logDeviceOut(deviceId)
    }

}
