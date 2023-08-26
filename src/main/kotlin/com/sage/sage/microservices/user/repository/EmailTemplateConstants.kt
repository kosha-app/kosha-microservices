package com.sage.sage.microservices.user.repository

object EmailTemplateConstants {
    const val VERIFICATION_EMAIL_SUBJECT = "Your OTP for Registration - [Your App Name]"
    const val VERIFICATION_EMAIL_BODY = "Your OTP for registration is: %s\n" +
            "Please use this code to complete your registration process.\n" +
            "\n" +
            "This code will expire in {EXPIRY_DURATION} minutes.\n" +
            "\n" +
            "If you did not request this registration, please ignore this message.\n" +
            "\n" +
            "Thank you for joining us!"
}

