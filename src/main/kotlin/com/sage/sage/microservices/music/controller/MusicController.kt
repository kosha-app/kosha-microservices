package com.sage.sage.microservices.music.controller

import com.sage.sage.microservices.music.model.request.AlbumModel
import com.sage.sage.microservices.music.service.MusicService
import com.sage.sage.microservices.music.model.request.TrackResponse
import com.sage.sage.microservices.music.model.response.SearchAlbumsResponse
import com.sage.sage.microservices.music.model.response.SearchTracksResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/music")
class MusicController(private val musicService: MusicService) {


    @GetMapping("/track/{trackId}")
    fun getTrack(@PathVariable trackId: String): ResponseEntity<TrackResponse> {
        return musicService.getTrack(trackId)
    }

    @PostMapping("album/add")
    fun createAlbum(@RequestBody albumRequest: AlbumModel): ResponseEntity<String>{
       return musicService.createAlbum(albumRequest)
    }

    @GetMapping("/album/{albumId}")
    fun getAlbum(@PathVariable albumId: String): ResponseEntity<AlbumModel?> {
        return musicService.getAlbum(albumId)
    }

    @GetMapping("album/search/{query}")
    fun searchAlbums(@PathVariable query: String): ResponseEntity<SearchAlbumsResponse>{
        return musicService.searchAlbums(query)
    }

    @GetMapping("track/search/{query}")
    fun searchTracks(@PathVariable query: String): ResponseEntity<SearchTracksResponse>{
        return musicService.searchTrack(query)
    }

}