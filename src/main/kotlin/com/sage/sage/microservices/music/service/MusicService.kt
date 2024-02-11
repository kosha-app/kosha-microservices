package com.sage.sage.microservices.music.service

import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import com.sage.sage.microservices.exception.exceptionobjects.McaHttpResponseCode
import com.sage.sage.microservices.music.model.request.*
import com.sage.sage.microservices.music.model.response.*
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
            .switchIfEmpty(Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "")))
            .next()
    }

    fun trackPlayed(trackId: String): Mono<Void> {
        return musicRepository.getAllAlbums()
            .flatMapIterable { album ->
                album.tracks.map { track ->
                    Pair(track, album.id)
                }
            }
            .filter { (track, _) ->
                track.id == trackId
            }
            .switchIfEmpty(
                Mono.defer {
                    Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, ""))
                }
            )
            .flatMap { pair ->
                musicRepository.updateTrackPlayed(
                    albumId = pair.second.toString(),
                    trackId = pair.first.id.toString()
                )
            }
            .then()
    }

    fun getMostPlayedArtists(): Mono<PopularArtistResponse> {
        return musicRepository.getAllAlbums()
            .flatMapIterable { album -> album.tracks }
            .flatMap { track -> Flux.fromIterable(track.trackArtist.toString().split(","))
                .map { it.trim() }
                .map { Pair(it, track.played ?: 0) }
            }
            .groupBy { it.first }
            .flatMap { group ->
                group
                    .reduce(0) { count, pair -> count + pair.second }
                    .map { Pair(group.key(), it) }
            }
            .collectList()
            .map {
                val popularArtists = it.sortedByDescending { it.second }
                val mappedList = popularArtists.map { artist ->
                   Artist(
                       artistName = artist.first,
                       totalStreams = artist.second
                   )
                }
                PopularArtistResponse(mappedList.take(10))
            }
    }


    fun getArtistPopularTracks(artistName: String): Mono<ArtistPopularTracksResponse> {
        return musicRepository.getAllAlbums()
            .flatMapIterable { album ->
                album.tracks.map { track ->
                    Pair(track, album.coverUrl)
                }
            }
            .filter { (track, _) ->
                track.trackArtist?.split(",")?.find { artist -> artist.trim() == artistName } != null
                        || track.trackFeatures?.split(",")?.find { artist -> artist.trim() == artistName } != null
            }
            .map { (track, coverUrl) ->
                TrackModel2(
                    id = track.id,
                    trackName = track.trackName,
                    trackArtist = track.trackArtist,
                    trackUrl = track.trackUrl,
                    coverUrl = coverUrl,
                    played = track.played,
                    trackFeatures = track.trackFeatures
                )
            }
            .collectList()
            .map { tracks ->
                val mostPopular = tracks.sortedByDescending { track -> track.played }

                ArtistPopularTracksResponse(mostPopular.take(5))
            }
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
                (album.albumName.contains(query, ignoreCase = true) || album.albumArtist.contains(
                    query,
                    ignoreCase = true
                ))
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
            .filter { (track, _) ->
                track.trackName?.contains(query, ignoreCase = true) == true ||
                        track.trackArtist?.contains(query, ignoreCase = true) == true ||
                        track.trackFeatures?.contains(query, ignoreCase = true) == true
            }
            .map { (track, coverUrl) ->
                TrackModel2(
                    id = track.id,
                    trackName = track.trackName,
                    trackArtist = track.trackArtist,
                    trackUrl = track.trackUrl,
                    coverUrl = coverUrl,
                    played = track.played
                )
            }
            .collectList()
            .map { tracks -> SearchTracksResponse(tracks) }
    }
}