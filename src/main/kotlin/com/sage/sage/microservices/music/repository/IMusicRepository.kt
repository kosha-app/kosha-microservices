package com.sage.sage.microservices.music.repository

import com.sage.sage.microservices.music.model.request.AlbumModel
import com.sage.sage.microservices.music.model.request.AlbumModel2
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface IMusicRepository {

    fun createAlbum(albumRequest: AlbumModel): Mono<Void>

    fun getAlbum(albumId: String): Mono<AlbumModel2>

    fun getAllAlbums(): Flux<AlbumModel2>

    fun updateTrackPlayed(albumId: String, trackId: String): Mono<Void>
}