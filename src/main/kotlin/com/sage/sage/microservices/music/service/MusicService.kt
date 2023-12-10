package com.sage.sage.microservices.music.service

import com.azure.cosmos.CosmosException
import com.sage.sage.microservices.music.model.request.*
import com.sage.sage.microservices.music.repository.MusicRepository
import com.sage.sage.microservices.music.model.response.SearchAlbumsResponse
import com.sage.sage.microservices.music.model.response.SearchTracksResponse
import com.sage.sage.microservices.music.repository.IMusicRepository
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class MusicService(private val musicRepository: IMusicRepository) {

    fun getTrack(trackId: String): ResponseEntity<TrackModel2> {
        return try {
            val allAlbums = musicRepository.getAllAlbums()
            val track = getTrackById(allAlbums, trackId)
            if (track == null){
                ResponseEntity(null, HttpStatus.NOT_FOUND)
            } else {
                ResponseEntity(track, HttpStatus.OK)
            }
        } catch (e: CosmosException){
            ResponseEntity(null, HttpStatusCode.valueOf(e.statusCode))
        }
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
        albums: List<AlbumModel2>?,
        query: String
    ): List<AlbumModel2>? {
        return albums?.filter { album ->
            // Check if the albumName, albumArtist contains the query
            album.albumName.contains(query, ignoreCase = true) || album.albumArtist.contains(query, ignoreCase = true)
        }
    }

    private fun searchTracks(
        albums: List<AlbumModel2>?,
        query: String
    ): List<TrackModel2> {
        val matchingTracks = mutableListOf<TrackModel2>()

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

    fun getTrackById(albums: List<AlbumModel2>?, trackId: String): TrackModel2? {
        var returnTrack : TrackModel2? = null

        albums?.forEach { album ->
            album.tracks.forEach { track ->
                if (track.id == trackId) {
                   returnTrack = TrackModel2(
                       id = track.id,
                       trackName = track.trackName,
                       trackArtist = track.trackArtist,
                       trackUrl = track.trackUrl,
                       coverUrl = album.coverUrl
                   )
                }
            }
        }
        return returnTrack
    }
}