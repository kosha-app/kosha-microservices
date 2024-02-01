package com.sage.sage.microservices.exception

import com.azure.cosmos.CosmosException
import com.azure.cosmos.implementation.ConflictException
import com.azure.cosmos.implementation.NotFoundException
import com.sage.sage.microservices.exception.exceptionobjects.ApiError
import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

private const val DEFAULT_MESSAGE = "We are experiencing problems in processing your request. Please try again."

@Slf4j
@ControllerAdvice
class KoshaExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(KoshaExceptionHandler::class.java)

    private fun buildResponseEntity(apiError: ApiError): ResponseEntity<Any> {
        return ResponseEntity(apiError, HttpStatusCode.valueOf(apiError.status))
    }

    @ExceptionHandler(value = [ConflictException::class])
     fun handleEntityConflict( exception: ConflictException): ResponseEntity<Any> {
        logger.error("Handling ConflictException: ${exception.shortMessage}", exception)
        val apiError = ApiError(
            status = exception.statusCode,
            timestamp = LocalDateTime.now(),
            message = "Already Exists"
        )
        return buildResponseEntity(apiError)
    }

    @ExceptionHandler(value = [NotFoundException::class])
    fun handleCosmoNotFound( exception: NotFoundException): ResponseEntity<Any> {
        logger.error("Handling NotFoundException: ${exception.shortMessage}", exception)
        val apiError = ApiError(
            status = exception.statusCode,
            timestamp = LocalDateTime.now(),
            message = "Does not exist"
        )
        return buildResponseEntity(apiError)
    }

    @ExceptionHandler(value = [CosmosException::class])
    fun handleCosmoDefault( exception: CosmosException): ResponseEntity<Any> {
        logger.error("Handling ConflictException: ${exception.shortMessage}", exception)
        val apiError = ApiError(
            status = exception.statusCode,
            timestamp = LocalDateTime.now(),
            message = DEFAULT_MESSAGE
        )
        return buildResponseEntity(apiError)
    }

    @ExceptionHandler(value = [KoshaGatewayException::class])
    fun handleIncorrectPassword(exception: KoshaGatewayException): ResponseEntity<Any>{
        logger.error("Handling ConflictException: ${exception.localizedMessage}", exception)
        val apiError = ApiError(
            status = exception.code.httpStatus.value(),
            timestamp = LocalDateTime.now(),
            message = exception.message.toString()
        )
        return buildResponseEntity(apiError)
    }
}