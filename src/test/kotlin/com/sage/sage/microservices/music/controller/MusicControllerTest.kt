//package com.sage.sage.microservices.music.controller
//
//import com.sage.sage.microservices.device.controller.DeviceController
//import com.sage.sage.microservices.device.service.DeviceService
//import com.sage.sage.microservices.music.model.request.AlbumModel
//import com.sage.sage.microservices.music.model.request.AlbumModel2
//import com.sage.sage.microservices.music.model.request.TrackModel
//import com.sage.sage.microservices.music.model.request.TrackModel2
//import com.sage.sage.microservices.music.model.response.*
//import com.sage.sage.microservices.music.service.MusicService
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
//import org.springframework.boot.test.mock.mockito.MockBean
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.web.reactive.server.WebTestClient
//import reactor.core.publisher.Mono
//
//@WebFluxTest
//@ActiveProfiles("test")
//@ContextConfiguration(classes = [MusicController::class])
//class MusicControllerTest {
//
//    @Autowired
//    private lateinit var webTestClient: WebTestClient
//
//    @MockBean
//    private lateinit var service: MusicService
//
//    @Test
//    fun `test getTrack endpoint`(){
//        val trackId = "track-id"
//        val track = TrackModel2(trackId, "", "", "", 0, "", "")
//        Mockito.`when`(service.getTrack(trackId)).thenReturn(Mono.just(track))
//        webTestClient
//            .get()
//            .uri("/music/track/track-id")
//            .exchange()
//            .expectStatus()
//            .isOk
//            .expectBody(TrackModel2::class.java)
//
//        Mockito.verify(service).getTrack(trackId)
//    }
//
//    @Test
//    fun `test createAlbum endpoint`(){
//        val album = AlbumModel("album-id","","AlbumName","AlbumArtist","","", listOf(TrackModel("trackId", "TrackName", "ArtistName", "trackUrl", 0, "")))
//        Mockito.`when`(service.createAlbum(album)).thenReturn(Mono.empty())
//        webTestClient
//            .post()
//            .uri("/music/album/add")
//            .bodyValue(album)
//            .exchange()
//            .expectStatus()
//            .isOk
//            .expectBody(Mono::class.java)
//    }
//
//    @Test
//    fun `test getAlbum endpoint`(){
//        val albumId = "album-id"
//        val album = AlbumModel2("album-id","","AlbumName","AlbumArtist","","", listOf(TrackModel2("trackId", "TrackName", "ArtistName", "trackUrl",0,"")))
//        Mockito.`when`(service.getAlbum(albumId)).thenReturn(Mono.just(album))
//        webTestClient
//            .get()
//            .uri("/music/album/album-id")
//            .exchange()
//            .expectStatus()
//            .isOk
//            .expectBody(AlbumModel::class.java)
//
//        Mockito.verify(service).getAlbum(albumId)
//    }
//
//    @Test
//    fun `test searchAlbum endpoint`(){
//        val query = "ArtistName"
//        val album = AlbumModel2("album-id","","AlbumName","AlbumArtist","","", listOf(TrackModel2("trackId", "TrackName", "ArtistName", "trackUrl", 0, "")))
//        Mockito.`when`(service.searchAlbums(query)).thenReturn(Mono.just(SearchAlbumsResponse(listOf(album))))
//        webTestClient
//            .get()
//            .uri("/music/album/search/ArtistName")
//            .exchange()
//            .expectStatus()
//            .isOk
//            .expectBody(SearchAlbumsResponse::class.java)
//
//        Mockito.verify(service).searchAlbums(query)
//    }
//
//    @Test
//    fun `test searchATrack endpoint`(){
//        val query = "ArtistName"
//        val track = TrackModel2("trackId", "TrackName", "ArtistName", "trackUrl", 0, "")
//        Mockito.`when`(service.searchTrack(query)).thenReturn(Mono.just(SearchTracksResponse(listOf(track   ))))
//        webTestClient
//            .get()
//            .uri("/music/track/search/ArtistName")
//            .exchange()
//            .expectStatus()
//            .isOk
//            .expectBody(SearchTracksResponse::class.java)
//
//        Mockito.verify(service).searchTrack(query)
//    }
//
//    @Test
//    fun `test getPopularArtistsTracks endpoint`(){
//        val name = "ArtistName"
//        val track = TrackModel2("trackId", "TrackName", "ArtistName", "trackUrl", 0, "")
//        Mockito.`when`(service.getArtistPopularTracks(name)).thenReturn(Mono.just(ArtistPopularTracksResponse(listOf(track))))
//        webTestClient
//            .get()
//            .uri("/music/popular-artist-tracks/ArtistName")
//            .exchange()
//            .expectStatus()
//            .isOk
//            .expectBody(ArtistPopularTracksResponse::class.java)
//
//        Mockito.verify(service).getArtistPopularTracks(name)
//    }
//
//    @Test
//    fun `test getPopularArtists endpoint`(){
//        val artist = Artist("Artistname", 1234)
//        Mockito.`when`(service.getMostPlayedArtists()).thenReturn(Mono.just(PopularArtistResponse(listOf(artist))))
//        webTestClient
//            .get()
//            .uri("/music/popular-artist")
//            .exchange()
//            .expectStatus()
//            .isOk
//            .expectBody(PopularArtistResponse::class.java)
//
//        Mockito.verify(service).getMostPlayedArtists()
//    }
//
//    @Test
//    fun `test trackPlayed endpoint`(){
//        val trackId = "track-id"
//        Mockito.`when`(service.trackPlayed(trackId)).thenReturn(Mono.empty())
//        webTestClient
//            .put()
//            .uri("/music/played/track-id")
//            .exchange()
//            .expectStatus()
//            .isOk
//            .expectBody(Mono::class.java)
//
//        Mockito.verify(service).trackPlayed(trackId)
//    }
//}