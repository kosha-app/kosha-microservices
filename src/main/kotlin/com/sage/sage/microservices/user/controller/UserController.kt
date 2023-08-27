package com.sage.sage.microservices.user.controller

import com.sage.sage.microservices.user.model.ResponseType
import com.sage.sage.microservices.user.model.User
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
    fun createUseV2r( @RequestBody userRegistrationRequest: UserRegistrationRequestV2): ResponseEntity<String> {
        return userService.createV2(userRegistrationRequest)
    }

    @PostMapping("/signin")
    fun signUserInV2(@RequestBody userSignInRequest: UserSignInRequest): ResponseEntity<SignInResponse>{
        return userService.signUserInV2(userSignInRequest)
    }

//    @PutMapping("/verification/{username}")
//    fun otpVerification(@PathVariable username: String, @RequestBody request: UserVerificationRequest): ResponseEntity<String>{
//        return userService.otpVerification(username, request)
//    }

    @PutMapping("/update/name/{username}")
    fun updateName( @PathVariable username: String, @RequestBody userUpdateNameRequest: UserUpdateNameRequest): String {
        return userService.updateName(username, userUpdateNameRequest)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    @PutMapping("/update/surname/{username}")
    fun updateSurname( @PathVariable username: String, userUpdateSurnameRequest: UserUpdateSurnameRequest): String {
        return userService.updateSurname(username, userUpdateSurnameRequest)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    @PutMapping("/update/email/{username}")
    fun updateEmail( @PathVariable username: String, userUpdateEmailRequest: UserUpdateEmailRequest): String {
        return userService.updateEmail(username, userUpdateEmailRequest)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    @PutMapping("/update/password/{username}")
    fun updatePassword( @PathVariable username: String, userUpdatePasswordRequest: UserUpdatePasswordRequest): String {
        return userService.updatePassword(username, userUpdatePasswordRequest)
    }


    @Throws(InterruptedException::class, ExecutionException::class)
    @DeleteMapping("/delete/{username}")
    fun deleteUser( @PathVariable username: String): String {
        return userService.deleteByUsername(username)
    }

    @GetMapping("/test")
    fun test():ResponseEntity<TestResponse>{
        return ResponseEntity.ok(TestResponse(ResponseType.SUCCESS, "Test get enpoint is running Blah Blah", "Halalalalalalalalaala"))
    }

}