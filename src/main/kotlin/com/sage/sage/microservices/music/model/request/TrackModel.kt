package com.sage.sage.microservices.music.model.request

import com.google.cloud.firestore.DocumentSnapshot

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