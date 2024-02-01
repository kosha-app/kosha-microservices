package com.sage.sage.microservices.music.service

import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import com.sage.sage.microservices.exception.exceptionobjects.McaHttpResponseCode
import com.sage.sage.microservices.music.model.request.*
import com.sage.sage.microservices.music.model.response.SearchAlbumsResponse
import com.sage.sage.microservices.music.model.response.SearchTracksResponse
import com.sage.sage.microservices.music.repository.IMusicRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MusicService(private val musicRepository: IMusicRepository) {

    fun getTrack(trackId: String): Mono<TrackModel2> {
        return musicRepository.getAllAlbums()
            .flatMap { album -> Flux.fromIterable(album.tracks) }
            .filter { track -> track.id.equals(trackId) }
            .next()
            .switchIfEmpty(Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "")))
    }

    fun createAlbum(albumRequest: AlbumModel): Mono<Void> {
        return musicRepository.createAlbum(albumRequest)
    }

    fun getAlbum(albumId: String): Mono<AlbumModel> {
        return musicRepository.getAlbum(albumId)
            .switchIfEmpty(Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "")))
    }

    fun searchAlbums(query: String): Mono<SearchAlbumsResponse> {
        return musicRepository.getAllAlbums()
            .filter { album ->
                (album.albumName.contains(query, ignoreCase = true) || album.albumArtist.contains(query, ignoreCase = true))
                        && album.tracks.size != 1
            }
            .collectList()
            .map { albums -> SearchAlbumsResponse(albums) }
    }

    fun searchTrack(query: String): Mono<SearchTracksResponse> {
        return musicRepository.getAllAlbums()
            .flatMapIterable { album ->
                album.tracks.map { track ->
                    Pair(track, album.coverUrl)
                }
            }
            .filter { (track, _ ) -> track.trackName?.contains(query, ignoreCase = true) == true || track.trackArtist?.contains(query, ignoreCase = true) == true }
            .map { (track, coverUrl) ->
                TrackModel2(
                    id = track.id,
                    trackName = track.trackName,
                    trackArtist = track.trackArtist,
                    trackUrl = track.trackUrl,
                    coverUrl = coverUrl
                )
            }
            .collectList()
            .map { tracks -> SearchTracksResponse(tracks) }
    }
}