package com.sage.sage.microservices.user.model.request

class UserSignInRequest(val email: String, val password: String, val deviceId: String)