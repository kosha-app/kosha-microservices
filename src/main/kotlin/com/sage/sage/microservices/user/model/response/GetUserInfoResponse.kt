package com.sage.sage.microservices.user.model.response

import com.sage.sage.microservices.user.model.User

class GetUserInfoResponse(
    val name: String? = null,
    val email: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val cellNumber: String? = null,
    val message: String? = null
) {
    companion object {
        fun User.toGetUserInfo(): GetUserInfoResponse {
            return GetUserInfoResponse(
                name, email, dateOfBirth, gender, cellNumber
            )
        }
    }
}
