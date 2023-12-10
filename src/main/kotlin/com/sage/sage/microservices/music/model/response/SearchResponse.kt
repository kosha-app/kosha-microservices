package com.sage.sage.microservices.music.model.response

import com.sage.sage.microservices.music.model.request.*

class SearchAlbumsResponse(val albums: List<AlbumModel2>?)

class SearchTracksResponse(val tracks: List<TrackModel2>?)