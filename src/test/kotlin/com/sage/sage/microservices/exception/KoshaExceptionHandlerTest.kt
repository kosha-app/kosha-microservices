package com.sage.sage.microservices.exception

import com.azure.cosmos.implementation.BadRequestException
import com.azure.cosmos.implementation.ConflictException
import com.azure.cosmos.implementation.NotFoundException
import com.azure.cosmos.implementation.http.HttpHeaders
import com.sage.sage.microservices.exception.exceptionobjects.ApiError
import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import com.sage.sage.microservices.exception.exceptionobjects.McaHttpResponseCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class KoshaExceptionHandlerTest {

    @InjectMocks
    private lateinit var koshaExceptionHandler: KoshaExceptionHandler


    @Test
    fun `handleEntityConflict should return ResponseEntity with status CONFLICT`() {
        val conflictException = ConflictException("", HttpHeaders(), "")

        // Arrange
        val apiError = ApiError(
            status = HttpStatus.CONFLICT.value(),
            timestamp = LocalDateTime.now(),
            message = "Already Exists"
        )

        // Act
        val responseEntity: ResponseEntity<Any> = koshaExceptionHandler.handleEntityConflict(conflictException)

        // Assert
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat((responseEntity.body as ApiError).status.toString()).isEqualTo(apiError.status.toString())
        assertThat((responseEntity.body as ApiError).message).isEqualTo(apiError.message)
    }

    @Test
    fun `handleCosmoNotFound should return ResponseEntity with status NOT_FOUND`() {
        // Arrange
        val notFoundException = NotFoundException("")

        val apiError = ApiError(
            status = HttpStatus.NOT_FOUND.value(),
            timestamp = LocalDateTime.now(),
            message = "Does not exist"
        )

        // Act
        val responseEntity: ResponseEntity<Any> = koshaExceptionHandler.handleCosmoNotFound(notFoundException)

        // Assert
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat((responseEntity.body as ApiError).status.toString()).isEqualTo(apiError.status.toString())
        assertThat((responseEntity.body as ApiError).message).isEqualTo(apiError.message)
    }

    @Test
    fun `handleCosmoDefault should return ResponseEntity with default message and status`() {
        // Arrange
        val cosmosException = BadRequestException("")
        val apiError = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            timestamp = LocalDateTime.now(),
            message = "We are experiencing problems in processing your request. Please try again."
        )

        // Act
        val responseEntity: ResponseEntity<Any> = koshaExceptionHandler.handleCosmoDefault(cosmosException)

        // Assert
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat((responseEntity.body as ApiError).status.toString()).isEqualTo(apiError.status.toString())
        assertThat((responseEntity.body as ApiError).message).isEqualTo(apiError.message)
    }

    @Test
    fun `handleIncorrectPassword should return ResponseEntity with status from exception and message`() {
        // Arrange
        val koshaGatewayException = KoshaGatewayException(McaHttpResponseCode.ERROR_UNAUTHORISED, "")
        val apiError = ApiError(
            status = HttpStatus.UNAUTHORIZED.value(),
            timestamp = LocalDateTime.now(),
            message = koshaGatewayException.message.toString()
        )

        // Act
        val responseEntity: ResponseEntity<Any> = koshaExceptionHandler.handleIncorrectPassword(koshaGatewayException)

        // Assert
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat((responseEntity.body as ApiError).status.toString()).isEqualTo(apiError.status.toString())
        assertThat((responseEntity.body as ApiError).message).isEqualTo(apiError.message)
    }
}