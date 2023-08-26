package com.sage.sage.microservices.user.model.request

import com.google.cloud.firestore.annotation.DocumentId

data class UserUpdateNameRequest(
        var newName: String
)

class UserUpdateSurnameRequest(
        val newSurname: String
)

class UserUpdatePasswordRequest(
        val newPassword: String
)

class UserUpdateEmailRequest(
        val newEmail: String
)

class UserUpdateVerificationRequest(
        val newVerification: Boolean
)

class UserVerificationRequest(
        val otp: String
)