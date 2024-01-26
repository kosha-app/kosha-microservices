package com.sage.sage.microservices.music.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.music.model.request.TrackResponse.Companion.toTrackModel
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot

data class AlbumModel(
    @JsonProperty("id") var id: String?,
    @JsonProperty("albumKey") var albumKey: String?,
    @JsonProperty("albumName") val albumName: String,
    @JsonProperty("albumArtist") val albumArtist: String,
    @JsonProperty("releaseDate") val releaseDate: String,
    @JsonProperty("coverUrl") val coverUrl: String,
    @JsonProperty("tracks") val tracks: List<TrackModel>
)

data class AlbumModel2(
    @JsonProperty("id") var id: String?,
    @JsonProperty("albumKey") var albumKey: String?,
    @JsonProperty("albumName") val albumName: String,
    @JsonProperty("albumArtist") val albumArtist: String,
    @JsonProperty("releaseDate") val releaseDate: String,
    @JsonProperty("coverUrl") val coverUrl: String,
    @JsonProperty("tracks") val tracks: List<TrackModel2>
)