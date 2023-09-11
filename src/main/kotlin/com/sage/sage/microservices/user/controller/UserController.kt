package com.sage.sage.microservices.user.controller

import com.sage.sage.microservices.user.model.ResponseType
import com.sage.sage.microservices.user.model.response.SignInResponse
import com.sage.sage.microservices.user.service.UserService
import com.sage.sage.microservices.user.model.response.TestResponse
import com.sage.sage.microservices.user.model.request.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ExecutionException

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService)   {
    @PostMapping("/register")
    fun createUser( @RequestBody userRegistrationRequest: UserRegistrationRequestV2): ResponseEntity<String> {
        return userService.create(userRegistrationRequest)
    }

    @PostMapping("/checkemail/{email}")
    fun checkEmail(@PathVariable email: String): ResponseEntity<String> {
        return userService.checkEmail(email)
    }

    @PostMapping("/signin")
    fun signUserIn(@RequestBody userSignInRequest: UserSignInRequest): ResponseEntity<SignInResponse>{
        return userService.signUserIn(userSignInRequest)
    }

    @PostMapping("/resendOtp/{email}")
    fun resendOtp(@PathVariable email: String): ResponseEntity<String>{
        return userService.resendOtp(email)
    }

    @PutMapping("/verification/{email}")
    fun otpVerification(@PathVariable email: String, @RequestBody request: UserVerificationRequest): ResponseEntity<String>{
        return userService.otpVerification(email, request)
    }

    @PutMapping("/update/name/{email}")
    fun updateName( @PathVariable email: String, @RequestBody userUpdateNameRequest: UserUpdateNameRequest): ResponseEntity<String> {
        return userService.updateName(email, userUpdateNameRequest)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    @PutMapping("/update/surname/{email}")
    fun updateSurname( @PathVariable email: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): ResponseEntity<String> {
        return userService.updateSurname(email, userUpdateSurnameRequest)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    @PutMapping("/update/email/{email}")
    fun updateEmail( @PathVariable email: String, userUpdateEmailRequest: UserUpdateEmailRequest): ResponseEntity<String> {
        return userService.updateEmail(email, userUpdateEmailRequest)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    @PutMapping("/update/password/{email}")
    fun updatePassword( @PathVariable email: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): ResponseEntity<String> {
        return userService.updatePassword(email, userUpdatePasswordRequest)
    }


    @Throws(InterruptedException::class, ExecutionException::class)
    @DeleteMapping("/delete/{email}")
    fun deleteUser( @PathVariable email: String): ResponseEntity<String> {
        return userService.deleteByEmail(email)
    }

    @GetMapping("/test")
    fun test():ResponseEntity<TestResponse>{
        return ResponseEntity.ok(TestResponse(ResponseType.SUCCESS, "Test get enpoint is running Blah Blah", "Halalalalalalalalaala"))
    }

}