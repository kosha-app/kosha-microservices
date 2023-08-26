package com.sage.sage.microservices.user.model.response

import com.google.cloud.firestore.annotation.DocumentId

class DeviceModel (
    val deviceId: String,
    val isLoggedIn: Boolean,
    val userUsername: String
)