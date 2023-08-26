package com.sage.sage.microservices.user.model

import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.annotation.DocumentId

class User(
    @DocumentId
    val username: String,
    val name: String?,
    val surname: String?,
    val password: String?,
    val email: String?,
    val cellNumber: String?,
    val isVerified: Boolean?,
    val otp: String?
) {
    companion object {
        fun DocumentSnapshot.toUser(): User {
            return User(
                username = id,
                name = getString("name"),
                surname = getString("surname"),
                email = getString("email"),
                password = getString("password"),
                cellNumber = getString("cellNumber"),
                isVerified = getBoolean("isVerified"),
                otp = getString("otp")
            )
        }
    }
}


