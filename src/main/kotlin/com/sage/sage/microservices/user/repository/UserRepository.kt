package com.sage.sage.microservices.user.repository


import com.sage.sage.microservices.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, String>{

    fun findByEmail(email: String): Optional<User>

    fun existsByEmail(email: String): Boolean
}