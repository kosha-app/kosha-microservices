package com.sage.sage.microservices.music.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.music.model.request.*

class SearchAlbumsResponse(@JsonProperty("albums") val albums: List<AlbumModel2>?)

class SearchTracksResponse(@JsonProperty("tracks") val tracks: List<TrackModel2>?)