package com.sage.sage.microservices.exception.exceptionobjects

class KoshaGatewayException(val code: McaHttpResponseCode, message: String?) : Exception(message)
