package com.sage.sage.microservices.user.model.response

import com.sage.sage.microservices.user.model.ResponseType

interface IGeloResponse{
    val responseType: ResponseType
    val data: Any
    val message: String
}

