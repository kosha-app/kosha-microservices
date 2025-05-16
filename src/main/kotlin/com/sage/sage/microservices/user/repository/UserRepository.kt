package com.sage.sage.microservices.user.repository


import com.sage.sage.microservices.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, String>{

    /**
 * Retrieves a user by their email address.
 *
 * @param email The email address to search for.
 * @return An Optional containing the user if found, or empty if no user exists with the given email.
 */
fun findByEmail(email: String): Optional<User>

    /**
 * Checks if a user with the specified email exists in the database.
 *
 * @param email The email address to search for.
 * @return `true` if a user with the given email exists, otherwise `false`.
 */
fun existsByEmail(email: String): Boolean
}