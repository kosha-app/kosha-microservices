package com.sage.sage.microservices.user.service

import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import com.sage.sage.microservices.exception.exceptionobjects.McaHttpResponseCode
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.collections.ArrayList


class UserServiceTest {

    private val userRepository: UserRepository = mock(UserRepository::class.java)

    private val service: UserService = UserService(userRepository)

    @Test
    fun `create should return Mono empty`() {
        // Arrange
        val request = UserRegistrationRequestV2(
            "name",
            "password",
            "email@com.com",
            "2 May 2020",
            "Male",
            "0134567898",
            "device-id"
        )

        // Mocking the behavior of getDevice to return an empty Mono
        `when`(userRepository.create(request)).thenReturn(Mono.empty())

        // Act
        val result = service.create(request)

        // Assert
        StepVerifier.create(result)
            .expectComplete()
            .verify()
    }

    @Test
    fun `checkEmail should return CheckEmailResponse when user does not exist`() {
        // Mocking UserRepository's getByEmail to return an empty Mono
        `when`(userRepository.getByEmail(anyString())).thenReturn(Mono.empty())

        // Mocking UserRepository's sendOtp to return a predefined OTP ID
        `when`(userRepository.sendOtp(anyString(), anyString())).thenReturn(Mono.just("123456"))

        // Call the checkEmail method
        val result = service.checkEmail("test@example.com")

        // Verify the result using StepVerifier
        StepVerifier.create(result)
            .expectNextMatches { response -> response.id == "123456" }
            .verifyComplete()

        // Verify that getByEmail was called with the lowercase email
        verify(userRepository).getByEmail("test@example.com")
        // Verify that sendOtp was called with anyString()
        verify(userRepository).sendOtp(anyString(), anyString())
        // Verify that getByEmail was called only once
        verify(userRepository, times(1)).getByEmail(anyString())
        // Verify that sendOtp was called only once
        verify(userRepository, times(1)).sendOtp(anyString(), anyString())
    }

    @Test
    fun `checkEmail should return KoshaGatewayException when user already exists`() {
        // Mocking UserRepository's getByEmail to return a non-empty Mono
        val devices = ArrayList<DeviceRequest>()
        devices.add(DeviceRequest("device-id"))
        val user = User(
            "user-id",
            "key",
            "name",
            "password",
            "test@example.com",
            "2 May 2020",
            "Male",
            "0134567898",
            devices,
            false
        )
        `when`(userRepository.getByEmail(anyString())).thenReturn(Mono.just(user))
        `when`(userRepository.sendOtp(anyString(), anyString())).thenReturn(Mono.just("123456"))

        // Call the checkEmail method
        val result = service.checkEmail("test@example.com")

        // Verify the result using StepVerifier
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()

        // Verify that getByEmail was called with the lowercase email
        verify(userRepository).getByEmail("test@example.com")
        // Verify that getByEmail was called only once
        verify(userRepository, times(1)).getByEmail(anyString())
    }

    @Test
    fun `otpVerification should return Mono empty when otp is verified`() {
        // Arrange
        val otpId = "otp-id"
        val request = UserVerificationRequest("123456")

        // Mocking the behavior of getDevice to return a Mono with an existing device
        `when`(userRepository.otpVerification(otpId, request)).thenReturn(Mono.just(true))

        // Act
        val result = service.otpVerification(otpId, request)

        // Assert
        StepVerifier.create(result)
            .expectComplete()
            .verify()
    }

    @Test
    fun `otpVerification should return Mono error when otp is not verified`() {
        // Arrange
        val otpId = "otp-id"
        val request = UserVerificationRequest("123456")

        // Mocking the behavior of getDevice to return a Mono with an existing device
        `when`(userRepository.otpVerification(otpId, request)).thenReturn(Mono.just(false))

        // Act
        val result = service.otpVerification(otpId, request)

        // Assert
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()
    }

    @Test
    fun `signUserIn should return DefaultResponse when user exists and password is correct`() {
        // Mocking UserRepository's getByEmail to return a user with a specific password
        val devices = ArrayList<DeviceRequest>()
        devices.add(DeviceRequest("device-id"))
        val user = User(
            "user-id",
            "key",
            "name",
            "correctPassword",
            "test@example.com",
            "2 May 2020",
            "Male",
            "0134567898",
            devices,
            false
        )
        `when`(userRepository.getByEmail("test@example.com")).thenReturn(
            Mono.just(user)
        )

        // Mocking UserRepository's createDevice and addDevice methods to return completed Mono
        `when`(userRepository.createDevice(DeviceModel("device-id2", "", "user-id"))).thenReturn(Mono.empty())
        `when`(userRepository.addDevice("test@example.com", DeviceRequest("device-id2"))).thenReturn(Mono.empty())

        // Call the signUserIn method
        val result = service.signUserIn(
            UserSignInRequest(
                email = "test@example.com",
                password = "correctPassword",
                "device-id2"
            )
        )

        // Verify the result using StepVerifier
        StepVerifier.create(result)
            .expectNextMatches { response -> response.message == "" }
            .verifyComplete()
    }

    @Test
    fun `signUserIn should return error when user exists and password is incorrect`() {
        // Mocking UserRepository's getByEmail to return a user with a specific password
        val devices = ArrayList<DeviceRequest>()
        devices.add(DeviceRequest("device-id"))
        val user = User(
            "user-id",
            "key",
            "name",
            "correctPassword",
            "test@example.com",
            "2 May 2020",
            "Male",
            "0134567898",
            devices,
            false
        )
        `when`(userRepository.getByEmail("test@example.com")).thenReturn(
            Mono.just(user)
        )

        // Mocking UserRepository's createDevice and addDevice methods to return completed Mono
        `when`(userRepository.createDevice(DeviceModel("device-id2", "", "user-id"))).thenReturn(Mono.empty())
        `when`(userRepository.addDevice("test@example.com", DeviceRequest("device-id2"))).thenReturn(Mono.empty())

        // Call the signUserIn method
        val result = service.signUserIn(
            UserSignInRequest(
                email = "test@example.com",
                password = "incorrectPassword",
                "device-id2"
            )
        )

        // Verify the result using StepVerifier
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()
    }

    @Test
    fun `signUserIn should return error when user does not exists`() {
        // Mocking UserRepository's getByEmail to return a user with a specific password
        `when`(userRepository.getByEmail("test@example.com")).thenReturn(
            Mono.empty()
        )

        // Mocking UserRepository's createDevice and addDevice methods to return completed Mono
        `when`(userRepository.createDevice(DeviceModel("device-id2", "", "user-id"))).thenReturn(Mono.empty())
        `when`(userRepository.addDevice("test@example.com", DeviceRequest("device-id2"))).thenReturn(Mono.empty())

        // Call the signUserIn method
        val result = service.signUserIn(
            UserSignInRequest(
                email = "test@example.com",
                password = "correctPassword",
                "device-id2"
            )
        )

        // Verify the result using StepVerifier
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()
    }

    @Test
    fun `getUserInfo should return Mono GetUserInfoResponse`() {
        // Arrange
        val devices = ArrayList<DeviceRequest>()
        devices.add(DeviceRequest("device-id"))
        val user = User(
            "user-id",
            "key",
            "name",
            "correctPassword",
            "test@example.com",
            "2 May 2020",
            "Male",
            "0134567898",
            devices,
            false
        )

        // Mocking the behavior of getDevice to return a Mono with an existing device
        `when`(userRepository.getProfileByUserId("user-id")).thenReturn(Mono.just(user))

        // Act
        val result = service.getUserInfo("user-id")

        // Assert
        StepVerifier.create(result)
            .assertNext {
                Assertions.assertEquals(it.name, user.name)
                Assertions.assertEquals(it.email, user.email)
                Assertions.assertEquals(it.gender, user.gender)
                Assertions.assertEquals(it.cellNumber, user.cellNumber)
                Assertions.assertEquals(it.dateOfBirth, user.dateOfBirth)
            }
            .verifyComplete()
    }

    @Test
    fun `updateName should return Mono empty`() {
        // Arrange
        val email = "email@exampl.com"

        `when`(userRepository.updateName(email, UserUpdateNameRequest("new"))).thenReturn(Mono.empty())

        // Act
        val result = service.updateName(email, UserUpdateNameRequest("new"))

        // Assert
        StepVerifier.create(result)
            .expectComplete()
            .verify()
    }

    @Test
    fun `updateName should return an error when user is not found`() {
        // Arrange
        val email = "nonexistent@example.com"
        val newName = "newPName"
        val userUpdateNameRequest = UserUpdateNameRequest(newName)

        `when`(userRepository.updateName(email, userUpdateNameRequest)).thenReturn(Mono.error(
            KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "User not found")
        ))

        // Act
        val result = service.updateName(email, userUpdateNameRequest)

        // Assert
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()
    }

    @Test
    fun `updatePassword should update password successfully`() {
        // Arrange
        val email = "test@example.com"
        val newPassword = "newPassword"
        val userUpdatePasswordRequest = UserUpdatePasswordRequest(newPassword)

        `when`(userRepository.updatePassword(email, userUpdatePasswordRequest)).thenReturn(Mono.empty())

        // Act
        val result = service.updatePassword(email, userUpdatePasswordRequest)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun `updatePassword should return an error when user is not found`() {
        // Arrange
        val email = "nonexistent@example.com"
        val newPassword = "newPassword"
        val userUpdatePasswordRequest = UserUpdatePasswordRequest(newPassword)

        `when`(userRepository.updatePassword(email, userUpdatePasswordRequest)).thenReturn(Mono.error(
            KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "User not found")
        ))

        // Act
        val result = service.updatePassword(email, userUpdatePasswordRequest)

        // Assert
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()
    }

    @Test
    fun `updateEmail should update email successfully`() {
        // Arrange
        val currentEmail = "old@example.com"
        val newEmail = "new@example.com"
        val userUpdateEmailRequest = UserUpdateEmailRequest(newEmail)

        `when`(userRepository.updateEmail(currentEmail, userUpdateEmailRequest)).thenReturn(Mono.empty())

        // Act
        val result = service.updateEmail(currentEmail, userUpdateEmailRequest)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun `updateEmail should return an error when user is not found`() {
        // Arrange
        val currentEmail = "nonexistent@example.com"
        val newEmail = "new@example.com"
        val userUpdateEmailRequest = UserUpdateEmailRequest(newEmail)

        `when`(userRepository.updateEmail(currentEmail, userUpdateEmailRequest)).thenReturn(Mono.error(
            KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "User not found")
        ))

        // Act
        val result = service.updateEmail(currentEmail, userUpdateEmailRequest)

        // Assert
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()
    }

    @Test
    fun `deleteByEmail should return Mono empty`() {
        // Arrange
        val email = "email@exampl.com"

        // Mocking the behavior of getDevice to return a Mono with an existing device
        `when`(userRepository.deleteByEmail(email)).thenReturn(Mono.empty())

        // Act
        val result = service.deleteByEmail(email)

        // Assert
        StepVerifier.create(result)
            .expectComplete()
            .verify()
    }

}