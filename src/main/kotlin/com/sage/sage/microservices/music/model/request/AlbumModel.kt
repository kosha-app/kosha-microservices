package com.sage.sage.microservices.music.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.music.model.request.TrackResponse.Companion.toTrackModel
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot

data class AlbumResponse(
    val id: String?,
    val albumName: String?,
    val albumArtist: String?,
    val releaseDate: String?,
    val coverUrl: String?,
    val tracks: List<TrackResponse>,
    val message: String = ""
){
    companion object{ fun DocumentSnapshot.toAlbumModel(): AlbumResponse {
        val listTracks = ArrayList<TrackResponse>()
        val tracks: List<DocumentReference> = get("tracks") as List<DocumentReference>

        for (track in tracks){
            listTracks.add(track.get().get().toTrackModel())
        }
            return AlbumResponse(
                id = id,
                albumName = getString("name"),
                albumArtist = getString("albumArtist"),
                releaseDate = getString("releaseDate"),
                coverUrl = getString("coverUrl"),
                tracks = listTracks,
                message = "Got Album: ${ getString("name")}"
            )
        }
    }
}


data class AlbumModel(
    @JsonProperty("id") var id: String?,
    @JsonProperty("albumKey") var albumKey: String?,
    @JsonProperty("albumName") val albumName: String,
    @JsonProperty("albumArtist") val albumArtist: String,
    @JsonProperty("releaseDate") val releaseDate: String,
    @JsonProperty("coverUrl") val coverUrl: String,
    @JsonProperty("tracks") val tracks: List<TrackModel>
)