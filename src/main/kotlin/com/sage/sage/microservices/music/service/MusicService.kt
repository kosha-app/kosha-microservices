package com.sage.sage.microservices.music.service

import com.sage.sage.microservices.music.repository.MusicRepository
import com.sage.sage.microservices.music.model.request.AlbumResponse
import com.sage.sage.microservices.music.model.request.TrackResponse
import com.sage.sage.microservices.music.repository.IMusicRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class MusicService(private val musicRepository: IMusicRepository) {

    fun getTrack(trackId: String): ResponseEntity<TrackResponse> {
        val track = musicRepository.getTrack(trackId)
        val model = TrackResponse(
            trackId = track.trackId,
            trackName = track.trackName,
            trackArtist = track.trackArtist,
            trackUrl = track.trackUrl,
        )
        return ResponseEntity(model, HttpStatus.OK)
    }

    fun getAlbum(albumId: String): ResponseEntity<AlbumResponse> {
        val album = musicRepository.getAlbum(albumId)
        val model = AlbumResponse(
            id = album.id,
            albumName = album.albumName,
            releaseDate = album.releaseDate,
            albumArtist = album.albumArtist,
            coverUrl = album.coverUrl,
            tracks = album.tracks,
            message = album.message
        )
        return ResponseEntity(model, HttpStatus.OK)
    }
}