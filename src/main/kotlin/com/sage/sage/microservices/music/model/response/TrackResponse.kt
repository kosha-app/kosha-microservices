package com.sage.sage.microservices.music.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.music.model.request.TrackModel2

class ArtistPopularTracksResponse(@JsonProperty("tracks") val tracks: List<TrackModel2>?)

class PopularArtistResponse(@JsonProperty("artists") val artists: List<Artist>)

class Artist(
    @JsonProperty("artistName") val artistName: String,
    @JsonProperty("totalStreams") val totalStreams: Int,
)