package com.sage.sage.microservices.user.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.cloud.firestore.annotation.DocumentId
import com.sage.sage.microservices.user.model.User
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import reactor.core.publisher.Mono

class DeviceRequest (
    @JsonProperty("deviceId") val deviceId: String
)

@Entity
@Table(name="device")
class DeviceModel (
    @Id val id: String,
    val userId: String
){
    companion object {
        /**
         * Wraps this DeviceModel instance in a Reactor Mono.
         *
         * @return a Mono emitting a copy of this DeviceModel.
         */
        fun DeviceModel.toMono(): Mono<DeviceModel> {
            return Mono.just(
                DeviceModel(
                    id = id,
                    userId = userId
                )
            )
        }
    }
}