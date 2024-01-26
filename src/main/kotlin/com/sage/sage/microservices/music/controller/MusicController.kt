package com.sage.sage.microservices.music.controller

import com.sage.sage.microservices.music.model.request.*
import com.sage.sage.microservices.music.service.MusicService
import com.sage.sage.microservices.music.model.response.SearchAlbumsResponse
import com.sage.sage.microservices.music.model.response.SearchTracksResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/music")
class MusicController(private val musicService: MusicService) {


    @GetMapping("/track/{trackId}")
    fun getTrack(@PathVariable trackId: String): Mono<TrackModel2> {
        return musicService.getTrack(trackId)
    }

    @PostMapping("album/add")
    fun createAlbum(@RequestBody albumRequest: AlbumModel): Mono<Void>{
       return musicService.createAlbum(albumRequest)
    }

    @GetMapping("/album/{albumId}")
    fun getAlbum(@PathVariable albumId: String): Mono<AlbumModel> {
        return musicService.getAlbum(albumId)
    }

    @GetMapping("album/search/{query}")
    fun searchAlbums(@PathVariable query: String): Flux<AlbumModel2>{
        return musicService.searchAlbums(query)
    }

    @GetMapping("track/search/{query}")
    fun searchTracks(@PathVariable query: String): Flux<TrackModel2>{
        return musicService.searchTrack(query)
    }

}