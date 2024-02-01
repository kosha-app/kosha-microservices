package com.sage.sage.microservices.user.controller

import com.azure.core.annotation.Post
import com.azure.cosmos.CosmosException
import com.azure.cosmos.implementation.ConflictException
import com.azure.cosmos.implementation.CosmosError
import com.sage.sage.microservices.user.model.ResponseType
import com.sage.sage.microservices.user.model.request.*
import com.sage.sage.microservices.user.model.response.*
import com.sage.sage.microservices.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.concurrent.ExecutionException


@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService)   {

    @PostMapping("/register")
    fun registerUser( @RequestBody userRegistrationRequest: UserRegistrationRequestV2): Mono<Void> {
        return userService.create(userRegistrationRequest)
    }

    @PostMapping("/checkemail/{email}")
    fun checkEmail(@PathVariable email: String): Mono<CheckEmailResponse> {
        return userService.checkEmail(email)
    }

    @PostMapping("/signin")
    fun signUserIn(@RequestBody userSignInRequest: UserSignInRequest): Mono<DefaultResponse>{
        return userService.signUserIn(userSignInRequest)
    }

    @PostMapping("/resendOtp/{email}")
    fun resendOtp(@PathVariable email: String): Mono<String>{
        return userService.resendOtp(email)
    }

    @PostMapping("/verification/{id}")
    fun otpVerification(@PathVariable id: String, @RequestBody request: UserVerificationRequest): Mono<Void>{
        return userService.otpVerification(id, request)
    }

    @GetMapping("/profile/{userId}")
    fun getUserInfo(@PathVariable userId: String): Mono<GetUserInfoResponse>{
        return userService.getUserInfo(userId)
    }

    @PutMapping("/update/name/{email}")
    fun updateName( @PathVariable email: String, @RequestBody userUpdateNameRequest: UserUpdateNameRequest): Mono<Void> {
        return userService.updateName(email, userUpdateNameRequest)
    }

    @PutMapping("/update/email/{email}")
    fun updateEmail( @PathVariable email: String, userUpdateEmailRequest: UserUpdateEmailRequest): Mono<Void> {
        return userService.updateEmail(email, userUpdateEmailRequest)
    }

    @PutMapping("/update/password/{email}")
    fun updatePassword( @PathVariable email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): Mono<Void> {
        return userService.updatePassword(email, userUpdatePasswordRequest)
    }


    @DeleteMapping("/delete/{email}")
    fun deleteUser( @PathVariable email: String): Mono<Void> {
        return userService.deleteByEmail(email)
    }
}