package com.sage.sage.microservices.device.model.response

import com.google.cloud.firestore.DocumentSnapshot

class CheckDeviceResponse(
    val loggedIn: Boolean? = false,
    val userUsername: String? = "",
    val message: String = "Device is not registered"
) {
    companion object {
        fun DocumentSnapshot.toCheckDeviceResponse(message: String): CheckDeviceResponse {
            return CheckDeviceResponse(
                loggedIn = getBoolean("isLoggedIn"),
                userUsername = getString("userUsername"),
                message = message
            )
        }
    }
}