package com.sage.sage.microservices.exception.exceptionobjects

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import java.time.LocalDateTime


internal class ApiError(
    var status: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    val timestamp: LocalDateTime,
    var message: String = "Unexpected error"
)