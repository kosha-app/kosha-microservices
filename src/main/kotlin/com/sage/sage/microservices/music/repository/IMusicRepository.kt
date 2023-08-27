package com.sage.sage.microservices.music.repository

import com.sage.sage.microservices.music.model.request.AlbumModel
import com.sage.sage.microservices.music.model.request.AlbumResponse
import com.sage.sage.microservices.music.model.request.TrackResponse
import org.springframework.stereotype.Repository

@Repository
interface IMusicRepository {

    fun getTrack(trackID: String): TrackResponse

    fun createAlbum(albumRequest: AlbumModel): Int?

    fun getAlbum(albumId: String): AlbumModel?
}