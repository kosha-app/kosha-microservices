package com.sage.sage.microservices.music.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.cloud.firestore.DocumentSnapshot
import java.util.UUID

class TrackResponse(
    val trackId: String?,
    val trackName: String?,
    val trackArtist: String?,
    val trackUrl: String?
){
    companion object{
        fun DocumentSnapshot.toTrackModel(): TrackResponse {
            return TrackResponse(
                trackId = id,
                trackName = getString("name"),
                trackArtist = getString("artist"),
                trackUrl = getString("url")
            )
        }
    }
}

class TrackModel(
    @JsonProperty("id") var id: String?,
    @JsonProperty("trackName") val trackName: String?,
    @JsonProperty("trackArtist") val trackArtist: String?,
    @JsonProperty("trackUrl") val trackUrl: String?
){
    init {
        id = UUID.randomUUID().toString()
    }
}

class TrackModel2(
    @JsonProperty("id") var id: String?,
    @JsonProperty("trackName") val trackName: String?,
    @JsonProperty("trackArtist") val trackArtist: String?,
    @JsonProperty("trackUrl") val trackUrl: String?,
    @JsonProperty("coverUrl") val coverUrl: String? = null
)