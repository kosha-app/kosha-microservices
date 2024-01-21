package com.sage.sage.microservices.exception

class KoshaGatewayException(val code: McaHttpResponseCode, message: String?) : Exception(message)
