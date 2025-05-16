package com.sage.sage.microservices.music.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.cloud.firestore.DocumentSnapshot
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

class TrackResponse(
    val trackId: String?,
    val trackName: String?,
    val trackArtist: String?,
    val tracFeatures: String? = "",
    val trackUrl: String?
) {
    companion object {
        fun DocumentSnapshot.toTrackModel(): TrackResponse {
            return TrackResponse(
                trackId = id,
                trackName = getString("name"),
                trackArtist = getString("artist"),
                trackUrl = getString("url"),
                tracFeatures = getString("features")
            )
        }
    }
}

class TrackModel(
    @JsonProperty("id") var id: String,
    @JsonProperty("trackName") val trackName: String?,
    @JsonProperty("trackArtist") val trackArtist: String?,
    @JsonProperty("trackFeatures") val trackFeatures: String?,
    @JsonProperty("played") var played: Int?,
    @JsonProperty("trackUrl") val trackUrl: String?
) {
    init {
        id = UUID.randomUUID().toString()
        played = 0
    }

    companion object {
        fun TrackModel.toTrackModel2(albumId: String, coverUrl: String): TrackModel2{
            return TrackModel2(
                id = id,
                albumId = albumId,
                trackName = trackName,
                trackArtist = trackArtist,
                trackFeatures = trackFeatures,
                played = played,
                trackUrl = trackUrl,
                coverUrl = coverUrl,
            )
        }
    }
}

@Entity
@Table(name="track")
class TrackModel2(
    @Id var id: String,
    val albumId: String?,
    val trackName: String?,
    val trackArtist: String?,
    val trackFeatures: String? = "",
    var played: Int?,
    val trackUrl: String?,
    val coverUrl: String? = null
)


