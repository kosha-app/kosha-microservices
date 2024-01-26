package com.sage.sage.microservices.music.service

import com.azure.cosmos.CosmosException
import com.sage.sage.microservices.exception.KoshaGatewayException
import com.sage.sage.microservices.exception.McaHttpResponseCode
import com.sage.sage.microservices.music.model.request.*
import com.sage.sage.microservices.music.repository.MusicRepository
import com.sage.sage.microservices.music.model.response.SearchAlbumsResponse
import com.sage.sage.microservices.music.model.response.SearchTracksResponse
import com.sage.sage.microservices.music.repository.IMusicRepository
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
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
    }

    fun searchAlbums(query: String): Flux<AlbumModel2> {
        return musicRepository.getAllAlbums()
            .filter { album ->
                (album.albumName.contains(query, ignoreCase = true) || album.albumArtist.contains(query, ignoreCase = true))
                        && album.tracks.size != 1
            }
    }

    fun searchTrack(query: String): Flux<TrackModel2> {
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
    }

    private fun searchAlbums(
        albums: List<AlbumModel2>?,
        query: String
    ): Flux<AlbumModel2>? {
        val searchedAlbums = albums?.filter { album ->
            (album.albumName.contains(query, ignoreCase = true) || album.albumArtist.contains(query, ignoreCase = true)) && album.tracks.size != 1
        }
        if (searchedAlbums != null) {
            return Flux.fromIterable(searchedAlbums.toList())
        } else {
            return Flux.empty()
        }
    }

//    private fun searchTracks(
//        albums: List<AlbumModel2>?,
//        query: String
//    ): List<TrackModel2> {
//        val matchingTracks = mutableListOf<TrackModel2>()
//
//        albums?.forEach { album ->
//            album.tracks.forEach { track ->
//                // Check if the trackName or trackArtist contains the query
//                if (track.trackName?.contains(query, ignoreCase = true) == true ||
//                    track.trackArtist?.contains(query, ignoreCase = true) == true
//                ) {
//                    matchingTracks.add(
//                        TrackModel2(
//                            id = track.id,
//                            trackName = track.trackName,
//                            trackArtist = track.trackArtist,
//                            trackUrl = track.trackUrl,
//                            coverUrl = album.coverUrl
//                        )
//                    )
//                }
//            }
//        }
//
//        return matchingTracks
//    }
//
//    fun getTrackById(albums: List<AlbumModel2>?, trackId: String): TrackModel2? {
//        var returnTrack: TrackModel2? = null
//
//        albums?.forEach { album ->
//            album.tracks.forEach { track ->
//                if (track.id == trackId) {
//                    returnTrack = TrackModel2(
//                        id = track.id,
//                        trackName = track.trackName,
//                        trackArtist = track.trackArtist,
//                        trackUrl = track.trackUrl,
//                        coverUrl = album.coverUrl
//                    )
//                }
//            }
//        }
//        return returnTrack
//    }
}