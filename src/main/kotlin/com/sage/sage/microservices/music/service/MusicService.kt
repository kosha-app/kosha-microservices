package com.sage.sage.microservices.music.service

import com.azure.cosmos.CosmosException
import com.sage.sage.microservices.music.model.request.AlbumModel
import com.sage.sage.microservices.music.repository.MusicRepository
import com.sage.sage.microservices.music.model.request.AlbumResponse
import com.sage.sage.microservices.music.model.request.TrackResponse
import com.sage.sage.microservices.music.repository.IMusicRepository
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
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

    fun createAlbum(albumRequest: AlbumModel): ResponseEntity<String> {
        return try {
            musicRepository.createAlbum(albumRequest)
            ResponseEntity.ok("Album Added")
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }

    }

    fun getAlbum(albumId: String): ResponseEntity<AlbumModel?> {
       return try {
            val album = musicRepository.getAlbum(albumId)
           ResponseEntity(album, HttpStatus.OK)
        }catch (e: CosmosException){
            ResponseEntity(null, HttpStatusCode.valueOf(e.statusCode))
        }
    }
}