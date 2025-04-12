package com.sage.sage.microservices.music.controller

import com.sage.sage.microservices.music.model.request.*
import com.sage.sage.microservices.music.model.response.ArtistPopularTracksResponse
import com.sage.sage.microservices.music.model.response.PopularArtistResponse
import com.sage.sage.microservices.music.service.MusicService
import com.sage.sage.microservices.music.model.response.SearchAlbumsResponse
import com.sage.sage.microservices.music.model.response.SearchTracksResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
    fun getAlbum(@PathVariable albumId: String): Mono<AlbumModel2> {
        return musicService.getAlbum(albumId)
    }

    @GetMapping("album/search/{query}")
    fun searchAlbums(@PathVariable query: String): Mono<SearchAlbumsResponse> {
        return musicService.searchAlbums(query)
    }

    @GetMapping("track/search/{query}")
    fun searchTracks(@PathVariable query: String): Mono<SearchTracksResponse>{
        return musicService.searchTrack(query)
    }

    @PutMapping("/played/{trackId}")
    fun trackPlayed(@PathVariable trackId: String): Mono<Void> {
        return musicService.trackPlayed(trackId)
    }

//    @GetMapping("/playlists/editors-picks")
//    fun getEditorsPicks(@PathVariable trackId: String): Mono<Void> {
//        return Mono.empty()
//    }

//    @PutMapping("/playlists/recently-played")
//    fun updateRecentlyPlayer(@PathVariable trackId: String): Mono<Void> {
//        return Mono.empty()
//    }

//    @PutMapping("/last-played/{deiveid}")
//    fun updateLastPlayed(@PathVariable deviceId: String): Mono<Void> {
//        return Mono.empty()
//    }

//    @GetMapping("/playlists/recently-played")
//    fun getRecentlyPlayed(@PathVariable trackId: String): Mono<Void> {
//        return Mono.empty()
//    }

    @GetMapping("/popular-artist-tracks/{artist}")
    fun getPopularArtistsTracks(@PathVariable artist: String): Mono<ArtistPopularTracksResponse> {
        return musicService.getArtistPopularTracks(artist)
    }

    @GetMapping("/popular-artist")
    fun getPopularArtists(): Mono<PopularArtistResponse> {
        return musicService.getMostPlayedArtists()
    }

//    @GetMapping("/popular-releases")
//    fun getPopularAlbums(@PathVariable trackId: String): Mono<Void> {
//        return Mono.empty()
//    }

}