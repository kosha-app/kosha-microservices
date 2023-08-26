package com.sage.sage.microservices.music.controller

import com.sage.sage.microservices.music.service.MusicService
import com.sage.sage.microservices.music.model.request.AlbumResponse
import com.sage.sage.microservices.music.model.request.TrackResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/music")
class MusicController(private val musicService: MusicService) {

    @GetMapping("/track/{trackId}")
    fun getTrack(@PathVariable trackId: String): ResponseEntity<TrackResponse> {
        return musicService.getTrack(trackId)
    }

    @GetMapping("/album/{albumId}")
    fun getAlbum(@PathVariable albumId: String): ResponseEntity<AlbumResponse> {
        return musicService.getAlbum(albumId)
    }

}