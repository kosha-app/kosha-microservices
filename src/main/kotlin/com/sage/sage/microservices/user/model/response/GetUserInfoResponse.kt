package com.sage.sage.microservices.user.model.response

import com.sage.sage.microservices.user.model.User

class GetUserInfoResponse(
    val name: String? = null,
    val email: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val cellNumber: String? = null,
    val devices: List<DeviceModel>? = null
) {
    companion object {
        /**
         * Converts a User instance to a GetUserInfoResponse containing user details and associated devices.
         *
         * @return A GetUserInfoResponse populated with the user's name, email, date of birth, gender, cell number, and devices.
         */
        fun User.toGetUserInfo(): GetUserInfoResponse {
            return GetUserInfoResponse(
                name, email, dateOfBirth, gender, cellNumber, devices
            )
        }
    }
}
