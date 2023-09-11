package com.sage.sage.microservices.device.model.response

import com.google.cloud.firestore.DocumentSnapshot

class CheckDeviceResponse(
    val loggedIn: Boolean? = false,
    val message: String = "Device is not registered"
)