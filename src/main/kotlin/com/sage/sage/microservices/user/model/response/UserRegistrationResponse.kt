package com.sage.sage.microservices.user.model.response

import com.sage.sage.microservices.user.model.ResponseType

data class UserRegistrationResponse(
    override val responseType: ResponseType,
    override val data: Any,
    override val message: String
) :
    IGeloResponse