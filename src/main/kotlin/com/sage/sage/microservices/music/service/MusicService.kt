package com.sage.sage.microservices.music.service

import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import com.sage.sage.microservices.exception.exceptionobjects.McaHttpResponseCode
import com.sage.sage.microservices.music.model.request.*
import com.sage.sage.microservices.music.model.request.AlbumModel2.Companion.toMono
import com.sage.sage.microservices.music.model.request.TrackModel.Companion.toTrackModel2
import com.sage.sage.microservices.music.model.response.*
import com.sage.sage.microservices.music.repository.MusicRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MusicService(
    private val musicRepository: MusicRepository,
) {

    fun getTrack(trackId: String): Mono<TrackModel2> {
        return Flux.fromIterable(musicRepository.findAll())
            .flatMap { album -> Flux.fromIterable(album.tracks) }
            .filter { track -> track.id.equals(trackId) }
            .switchIfEmpty(Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "")))
            .next()
    }

    fun trackPlayed(trackId: String): Mono<Void> {
        return Flux.fromIterable(musicRepository.findAll())
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
            .then()
    }

    fun getMostPlayedArtists(): Mono<PopularArtistResponse> {
        return Flux.fromIterable(musicRepository.findAll())
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
                       totalStreams = artist.second,
                   )
                }
                PopularArtistResponse(mappedList.take(10))
            }
    }


    fun getArtistPopularTracks(artistName: String): Mono<ArtistPopularTracksResponse> {
        return Flux.fromIterable(musicRepository.findAll())
            .flatMapIterable { album ->
                album.tracks.map { track ->
                    Triple(track, album.coverUrl, album.id)
                }
            }
            .filter { (track, _, _) ->
                track.trackArtist?.split(",")?.find { artist -> artist.trim() == artistName } != null
                        || track.trackFeatures?.split(",")?.find { artist -> artist.trim() == artistName } != null
            }
            .map { (track, coverUrl, albumId) ->
                TrackModel2(
                    id = track.id,
                    trackName = track.trackName,
                    trackArtist = track.trackArtist,
                    trackUrl = track.trackUrl,
                    coverUrl = coverUrl,
                    played = track.played,
                    trackFeatures = track.trackFeatures,
                    albumId = albumId
                )
            }
            .collectList()
            .map { tracks ->
                val mostPopular = tracks.sortedByDescending { track -> track.played }

                ArtistPopularTracksResponse(mostPopular.take(5))
            }
    }

    fun createAlbum(albumRequest: AlbumModel): Mono<Void> {
        musicRepository.save(AlbumModel2(
            id = albumRequest.id,
            albumName = albumRequest.albumName,
            albumArtist = albumRequest.albumArtist,
            releaseDate = albumRequest.releaseDate,
            coverUrl = albumRequest.coverUrl,
            tracks = albumRequest.tracks.map { trackModel ->
                trackModel.toTrackModel2(albumId = albumRequest.id, coverUrl = albumRequest.coverUrl)
            },
        ))
        return Mono.empty()
    }

    fun getAlbum(albumId: String): Mono<AlbumModel2> {
        return musicRepository.getReferenceById(albumId).toMono()
            .switchIfEmpty(Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "")))
    }

    fun searchAlbums(query: String): Mono<SearchAlbumsResponse> {
        return Flux.fromIterable(musicRepository.findAll())
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
        return Flux.fromIterable(musicRepository.findAll())
            .flatMapIterable { album ->
                album.tracks.map { track ->
                    Triple(track, album.coverUrl, album.id)
                }
            }
            .filter { (track, _, _) ->
                track.trackName?.contains(query, ignoreCase = true) == true ||
                        track.trackArtist?.contains(query, ignoreCase = true) == true ||
                        track.trackFeatures?.contains(query, ignoreCase = true) == true
            }
            .map { (track, coverUrl, albumId) ->
                TrackModel2(
                    id = track.id,
                    trackName = track.trackName,
                    trackArtist = track.trackArtist,
                    trackUrl = track.trackUrl,
                    coverUrl = coverUrl,
                    played = track.played,
                    albumId = albumId,
                )
            }
            .collectList()
            .map { tracks -> SearchTracksResponse(tracks) }
    }
}