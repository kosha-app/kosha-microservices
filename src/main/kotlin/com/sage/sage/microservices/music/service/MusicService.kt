package com.sage.sage.microservices.music.service

import com.azure.cosmos.CosmosException
import com.sage.sage.microservices.music.model.request.AlbumModel
import com.sage.sage.microservices.music.repository.MusicRepository
import com.sage.sage.microservices.music.model.request.AlbumResponse
import com.sage.sage.microservices.music.model.request.TrackModel
import com.sage.sage.microservices.music.model.request.TrackResponse
import com.sage.sage.microservices.music.model.response.SearchAlbumsResponse
import com.sage.sage.microservices.music.model.response.SearchTracksResponse
import com.sage.sage.microservices.music.repository.IMusicRepository
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class MusicService(private val musicRepository: IMusicRepository) {

    fun getTrack(trackId: String): ResponseEntity<TrackResponse> {
        val track = musicRepository.getTrack(trackId)
        val model = TrackResponse(
            trackId = track.trackId,
            trackName = track.trackName,
            trackArtist = track.trackArtist,
            trackUrl = track.trackUrl,
        )
        return ResponseEntity(model, HttpStatus.OK)
    }

    fun createAlbum(albumRequest: AlbumModel): ResponseEntity<String> {
        return try {
            musicRepository.createAlbum(albumRequest)
            ResponseEntity.ok("Album Added")
        } catch (e: CosmosException) {
            ResponseEntity(e.shortMessage, HttpStatusCode.valueOf(e.statusCode))
        }

    }

    fun getAlbum(albumId: String): ResponseEntity<AlbumModel?> {
        return try {
            val album = musicRepository.getAlbum(albumId)
            ResponseEntity(album, HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(null, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun searchAlbums(query: String): ResponseEntity<SearchAlbumsResponse> {
        return try {
            val allAlbums = musicRepository.getAllAlbums()
            val searchedAlbums = searchAlbums(allAlbums, query)
            ResponseEntity(SearchAlbumsResponse(searchedAlbums), HttpStatus.OK)
        } catch (e: CosmosException) {
            ResponseEntity(null, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    fun searchTrack(query: String): ResponseEntity<SearchTracksResponse>{
        return try {
            val allAlbums = musicRepository.getAllAlbums()
            val searchedTracks = searchTracks(allAlbums, query)
            ResponseEntity(SearchTracksResponse(searchedTracks), HttpStatus.OK)
        } catch (e: CosmosException){
            ResponseEntity(null, HttpStatusCode.valueOf(e.statusCode))
        }
    }

    private fun searchAlbums(
        albums: List<AlbumModel>?,
        query: String
    ): List<AlbumModel>? {
        return albums?.filter { album ->
            // Check if the albumName, albumArtist contains the query
            album.albumName.contains(query, ignoreCase = true) || album.albumArtist.contains(query, ignoreCase = true)
        }
    }

    fun searchTracks(
        albums: List<AlbumModel>?,
        query: String
    ): List<TrackModel> {
        val matchingTracks = mutableListOf<TrackModel>()

        albums?.forEach { album ->
            album.tracks.forEach { track ->
                // Check if the trackName or trackArtist contains the query
                if (track.trackName?.contains(query, ignoreCase = true) == true ||
                    track.trackArtist?.contains(query, ignoreCase = true) == true
                ) {
                    matchingTracks.add(track)
                }
            }
        }

        return matchingTracks
    }
}