package com.sage.sage.microservices.music.model.response

import com.sage.sage.microservices.music.model.request.AlbumModel
import com.sage.sage.microservices.music.model.request.TrackModel
import com.sage.sage.microservices.music.model.request.TrackResponse

class SearchAlbumsResponse(val albums: List<AlbumModel>?)

class SearchTracksResponse(val tracks: List<TrackModel>?)