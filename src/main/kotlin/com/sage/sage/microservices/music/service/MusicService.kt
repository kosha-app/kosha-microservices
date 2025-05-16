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

    /**
     * Retrieves a track by its ID from all albums.
     *
     * @param trackId The unique identifier of the track to retrieve.
     * @return A Mono emitting the matching TrackModel2, or an error if not found.
     */
    fun getTrack(trackId: String): Mono<TrackModel2> {
        return Flux.fromIterable(musicRepository.findAll())
            .flatMap { album -> Flux.fromIterable(album.tracks) }
            .filter { track -> track.id.equals(trackId) }
            .switchIfEmpty(Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "")))
            .next()
    }

    /**
     * Completes successfully if a track with the given ID exists; otherwise, signals an error if not found.
     *
     * Checks for the existence of a track by its ID across all albums. If the track is not found, returns an error with `KoshaGatewayException` indicating the item was not found.
     *
     * @param trackId The unique identifier of the track to check.
     * @return A [Mono] that completes if the track exists, or emits an error if not found.
     */
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

    /**
     * Returns the top 10 most played artists based on the total play count across all tracks.
     *
     * Aggregates play counts for each artist by summing the number of times their tracks have been played,
     * sorts artists in descending order of total streams, and returns the result in a `PopularArtistResponse`.
     *
     * @return A `Mono` emitting a `PopularArtistResponse` containing the top 10 artists by total streams.
     */
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


    /**
     * Retrieves the most popular tracks for a given artist, including both main and featured appearances.
     *
     * Filters tracks across all albums where the specified artist is listed as either the main artist or a featured artist,
     * sorts them by play count in descending order, and returns the top five tracks in an `ArtistPopularTracksResponse`.
     *
     * @param artistName The name of the artist to search for.
     * @return A Mono emitting an `ArtistPopularTracksResponse` containing up to five of the artist's most played tracks.
     */
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

    /**
     * Saves a new album to the repository after converting it to the internal album model.
     *
     * Converts the provided album and its tracks to the internal data format before persisting.
     * Completes without emitting a value when the operation is finished.
     */
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

    /**
     * Retrieves an album by its ID.
     *
     * Returns a [Mono] emitting the album if found, or an error if no album with the specified ID exists.
     *
     * @param albumId The unique identifier of the album to retrieve.
     * @return A [Mono] containing the album, or an error if not found.
     */
    fun getAlbum(albumId: String): Mono<AlbumModel2> {
        return musicRepository.getReferenceById(albumId).toMono()
            .switchIfEmpty(Mono.error(KoshaGatewayException(McaHttpResponseCode.ERROR_ITEM_NOT_FOUND_EXCEPTION, "")))
    }

    /**
     * Searches for albums whose name or artist matches the given query, excluding albums with only one track.
     *
     * @param query The search string to match against album names and artists.
     * @return A Mono emitting a SearchAlbumsResponse containing the list of matching albums.
     */
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

    /**
     * Searches for tracks whose name, artist, or featured artists contain the given query string.
     *
     * Returns a response containing all matching tracks, including their album cover URL and album ID.
     *
     * @param query The search string to match against track name, artist, or featured artists.
     * @return A Mono emitting a SearchTracksResponse with the list of matching tracks.
     */
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