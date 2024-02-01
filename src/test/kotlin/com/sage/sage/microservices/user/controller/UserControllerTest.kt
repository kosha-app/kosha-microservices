package com.sage.sage.microservices.user.controller

import com.sage.sage.microservices.device.controller.DeviceController
import com.sage.sage.microservices.device.service.DeviceService
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.DefaultResponse
import com.sage.sage.microservices.user.model.response.GetUserInfoResponse
import com.sage.sage.microservices.user.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest
@ActiveProfiles("test")
@ContextConfiguration(classes = [UserController::class])
class UserControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var service: UserService

    @Test
    fun `test registerUser endpoint`(){
        val request = UserRegistrationRequestV2("name", "password", "email@example.com", "20 July  20200", "male", "0123456789", "fygdsouiyfrgbuysr")
        Mockito.`when`(service.create(request)).thenReturn(Mono.empty())
        webTestClient
            .post()
            .uri("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `test signUserIn endpoint`() {
        val request = UserSignInRequest("test@example.com", "password", "device-id")
        val response = DefaultResponse("User signed in successfully.")
        Mockito.`when`(service.signUserIn(request)).thenReturn(Mono.just(response))
        webTestClient
            .post()
            .uri("/user/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(DefaultResponse::class.java)
    }

    @Test
    fun `test resendOtp endpoint`() {
        val email = "test@example.com"
        val responseMessage = "OTP resent successfully."
        Mockito.`when`(service.resendOtp(email)).thenReturn(Mono.just(responseMessage))
        webTestClient
            .post()
            .uri("/user/resendOtp/{email}", email)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `test otpVerification endpoint`() {
        val id = "someId"
        val request = UserVerificationRequest("123456")
        Mockito.`when`(service.otpVerification("id",request)).thenReturn(Mono.empty())
        webTestClient
            .post()
            .uri("/user/verification/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `test getUserInfo endpoint`() {
        val userId = "someUserId"
        val response = GetUserInfoResponse("John Doe", "john@example.com", "Male", "Active")
        Mockito.`when`(service.getUserInfo(userId)).thenReturn(Mono.just(response))
        webTestClient
            .get()
            .uri("/user/profile/{userId}", userId)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(GetUserInfoResponse::class.java)

    }

    @Test
    fun `test updateName endpoint`() {
        val email = "test@example.com"
        val request = UserUpdateNameRequest("NewName")
        webTestClient
            .put()
            .uri("/user/update/name/{email}", email)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `test updateEmail endpoint`() {
        val email = "test@example.com"
        val request = UserUpdateEmailRequest("newemail@example.com")
        Mockito.`when`(service.updateEmail(email , request)).thenReturn(Mono.empty())
        webTestClient
            .put()
            .uri("/user/update/email/{email}", email)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `test updatePassword endpoint`() {
        val email = "test@example.com"
        val request = UserUpdatePasswordRequest( newPassword = "newPassword")
        Mockito.`when`(service.updatePassword(email ,request)).thenReturn(Mono.empty())
        webTestClient
            .put()
            .uri("/user/update/password/{email}", email)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `test deleteUser endpoint`() {
        val email = "test@example.com"
        webTestClient
            .delete()
            .uri("/user/delete/{email}", email)
            .exchange()
            .expectStatus()
            .isOk
    }
}