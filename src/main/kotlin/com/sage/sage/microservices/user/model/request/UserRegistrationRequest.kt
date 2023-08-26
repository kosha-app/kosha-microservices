package com.sage.sage.microservices.user.model.request

import com.sage.sage.microservices.user.model.response.DeviceModel
import com.google.cloud.firestore.annotation.DocumentId

class UserRegistrationRequest(
    @DocumentId val username: String,
    val name: String,
    val surname: String,
    val password: String,
    val email: String,
    val cellNumber: String,
    val devices: List<DeviceModel>
)