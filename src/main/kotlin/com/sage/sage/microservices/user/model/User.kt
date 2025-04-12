package com.sage.sage.microservices.user.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.model.response.DeviceRequest
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import reactor.core.publisher.Mono

@Entity
@Table(name="user")
class User(
    @Id val userId: String,
    val email: String,
    val name: String,
    val password: String,
    val dateOfBirth: String,
    val gender: String,
    val cellNumber: String?,
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    var devices: List<DeviceModel>
) {
    companion object {
        fun User.toMono(): Mono<User> {
            return Mono.just(
                User(
                   userId, email, name, password, dateOfBirth, gender, cellNumber, devices
                )
            )
        }
    }
}






