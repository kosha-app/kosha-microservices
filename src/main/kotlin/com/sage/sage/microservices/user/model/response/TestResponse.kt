package com.sage.sage.microservices.user.model.response

import com.sage.sage.microservices.user.model.ResponseType

class TestResponse (override val responseType: ResponseType, override val data: String, override val message: String):
    IGeloResponse
