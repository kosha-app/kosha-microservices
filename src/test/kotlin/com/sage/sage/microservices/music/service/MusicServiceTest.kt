package com.sage.sage.microservices.music.service

import com.sage.sage.microservices.exception.exceptionobjects.KoshaGatewayException
import com.sage.sage.microservices.music.model.request.AlbumModel
import com.sage.sage.microservices.music.model.request.AlbumModel2
import com.sage.sage.microservices.music.model.request.TrackModel
import com.sage.sage.microservices.music.model.request.TrackModel2
import com.sage.sage.microservices.music.model.response.Artist
import com.sage.sage.microservices.music.model.response.ArtistPopularTracksResponse
import com.sage.sage.microservices.music.model.response.PopularArtistResponse
import com.sage.sage.microservices.music.repository.IMusicRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


class MusicServiceTest {

    private val repository: IMusicRepository = Mockito.mock(IMusicRepository::class.java)

    private val service = MusicService(repository)

    @Test
    fun `getTrack should return Mono TrackModel2 for existent track`(){
        val trackId = "music-id"
        val listOfAlBums = listOf(
            AlbumModel2("","","","","","", listOf(TrackModel2(trackId, "TrackName", "ArtistName", "features",12,"trackUrl", "coverUrl"))),
            AlbumModel2("","","","","","", listOf(TrackModel2("trackId2", "TrackName2", "ArtistName2","features",12, "trackUrl2", "coverUrl2"))),
            AlbumModel2("","","","","","", listOf(TrackModel2("trackId3", "TrackName3", "ArtistName3","features",12, "trackUrl3", "coverUrl3"))),
            AlbumModel2("","","","","","", listOf(TrackModel2("trackId4", "TrackName4", "ArtistName4","features",12, "trackUrl4", "coverUrl4"))),
        )

        // Mocking the behavior of getDevice to return a Mono with an existing device
        Mockito.`when`(repository.getAllAlbums()).thenReturn(Flux.fromIterable(listOfAlBums))

        // Act
        val result = service.getTrack(trackId)

        // Assert
        StepVerifier.create(result)
            .assertNext {
                Assertions.assertEquals(it.id, trackId)
                Assertions.assertEquals(it.trackName, "TrackName")
                Assertions.assertEquals(it.trackArtist, "ArtistName")
                Assertions.assertEquals(it.trackUrl, "trackUrl")
                Assertions.assertEquals(it.coverUrl, "coverUrl")
            }
            .verifyComplete()
    }

    @Test
    fun `getTrack should return Mono error for non-existent track`(){
        val trackId = "music-id-non-existent"
        val listOfAlBums = listOf(
            AlbumModel2("","","","","","", listOf(TrackModel2("trackId", "TrackName", "ArtistName","features",12, "trackUrl", "coverUrl"))),
            AlbumModel2("","","","","","", listOf(TrackModel2("trackId2", "TrackName2", "ArtistName2","features",12, "trackUrl2", "coverUrl2"))),
            AlbumModel2("","","","","","", listOf(TrackModel2("trackId3", "TrackName3", "ArtistName3", "features",12,"trackUrl3", "coverUrl3"))),
            AlbumModel2("","","","","","", listOf(TrackModel2("trackId4", "TrackName4", "ArtistName4", "features",12,"trackUrl4", "coverUrl4"))),
        )

        // Mocking the behavior of getDevice to return a Mono with an existing device
        Mockito.`when`(repository.getAllAlbums()).thenReturn(Flux.fromIterable(listOfAlBums))

        // Act
        val result = service.getTrack(trackId)

        // Assert
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()
    }

    @Test
    fun `createAlbum should return Mono Void for existent album`(){
        val album = AlbumModel("album-id","","AlbumName","AlbumArtist","20 July 1999","CoverUrl", listOf(TrackModel("music-id", "TrackName", "ArtistName","features",12, "trackUrl")))


        // Mocking the behavior of getDevice to return a Mono with an existing device
        Mockito.`when`(repository.createAlbum(album)).thenReturn(Mono.empty())

        // Act
        val result = service.createAlbum(album)

        // Assert
        StepVerifier.create(result)
            .expectComplete()
            .verify()
    }

    @Test
    fun `getAlbum should return Mono AlbumModel for existent album`(){
        val album = AlbumModel2("album-id","","AlbumName","AlbumArtist","20 July 1999","CoverUrl", listOf(TrackModel2("music-id", "TrackName", "ArtistName","features",12, "trackUrl")))


        // Mocking the behavior of getDevice to return a Mono with an existing device
        Mockito.`when`(repository.getAlbum("album-id")).thenReturn(Mono.just(album))

        // Act
        val result = service.getAlbum("album-id")

        // Assert
        StepVerifier.create(result)
            .assertNext {
                Assertions.assertEquals(it.id , "album-id")
            }
            .verifyComplete()
    }

    @Test
    fun `getAlbum should return Mono error for non-existent album`(){
        // Mocking the behavior of getDevice to return a Mono with an existing device
        Mockito.`when`(repository.getAlbum("album-id")).thenReturn(Mono.empty())

        // Act
        val result = service.getAlbum("album-id")

        // Assert
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()
    }

    @Test
    fun `searchAlbum should return Mono SearchAlbumsResponse for queried albums`(){
        val query = "Artist2"
        val listOfAlBums = listOf(
            AlbumModel2("album-id","","AlbumName","AlbumArtist","","", listOf(TrackModel2("trackId", "TrackName", "ArtistName", "features",12,"trackUrl", "coverUrl"))),
            AlbumModel2("album-id2","","AlbumName2","AlbumArtist2","","", listOf(TrackModel2("trackId2", "TrackName2", "ArtistName2", "features",12,"trackUrl2", "coverUrl2"),TrackModel2("trackId2", "TrackName2", "ArtistName2", "features",12,"trackUrl2", "coverUrl2"))),
            AlbumModel2("album-id3","","AlbumName3","AlbumArtist3","","", listOf(TrackModel2("trackId3", "TrackName3", "ArtistName3", "features",12,"trackUrl3", "coverUrl3"))),
            AlbumModel2("album-id4","","AlbumName4","AlbumArtist4","","", listOf(TrackModel2("trackId4", "TrackName4", "ArtistName4", "features",12,"trackUrl4", "coverUrl4"))),
        )

        // Mocking the behavior of getDevice to return a Mono with an existing device
        Mockito.`when`(repository.getAllAlbums()).thenReturn(Flux.fromIterable(listOfAlBums))

        // Act
        val result = service.searchAlbums(query)

        // Assert
        StepVerifier.create(result)
            .assertNext {
                Assertions.assertEquals(it.albums?.size, 1)
                Assertions.assertEquals(it.albums?.get(0)?.id, "album-id2")
            }
            .verifyComplete()
    }

    @Test
    fun `searchTrack should return Mono SearchTrackResponse for queried tracks`(){
        val query = "Name3"
        val listOfAlBums = listOf(
            AlbumModel2("album-id","","AlbumName","AlbumArtist","","", listOf(TrackModel2("trackId", "TrackName", "ArtistName", "features",12,"trackUrl", "coverUrl"))),
            AlbumModel2("album-id2","","AlbumName2","AlbumArtist2","","", listOf(TrackModel2("trackId2", "TrackName2", "ArtistName2", "features",12,"trackUrl2", "coverUrl2"))),
            AlbumModel2("album-id3","","AlbumName3","AlbumArtist3","","", listOf(TrackModel2("trackId3", "TrackName3", "ArtistName3", "features",12,"trackUrl3", "coverUrl3"))),
            AlbumModel2("album-id4","","AlbumName4","AlbumArtist4","","", listOf(TrackModel2("trackId4", "TrackName4", "ArtistName4", "features",12,"trackUrl4", "coverUrl4"))),
        )

        // Mocking the behavior of getDevice to return a Mono with an existing device
        Mockito.`when`(repository.getAllAlbums()).thenReturn(Flux.fromIterable(listOfAlBums))

        // Act
        val result = service.searchTrack(query)

        // Assert
        StepVerifier.create(result)
            .assertNext {
                Assertions.assertEquals(it.tracks?.size, 1)
                Assertions.assertEquals(it.tracks?.get(0)?.id, "trackId3")
            }
            .verifyComplete()
    }

    @Test
    fun `getMostPlayedArtists should return popular artists`() {
        // Mocking repository response
        val albums = listOf(
            AlbumModel2("album-id","","AlbumName","AlbumArtist","","", listOf(TrackModel2("trackId", "TrackName", "ArtistName", "features",100,"trackUrl", "coverUrl")))
        )
        Mockito.`when`(repository.getAllAlbums()).thenReturn(Flux.fromIterable(albums))

        // Act
        val result = service.getMostPlayedArtists()

        // Assert
        StepVerifier.create(result)
            .assertNext { artists ->
                Assertions.assertEquals(artists.artists.size, 1)
                Assertions.assertEquals(artists.artists[0].artistName, "ArtistName")
                Assertions.assertEquals(artists.artists[0].totalStreams, 100)
            }
            .verifyComplete()
    }

    @Test
    fun `getArtistPopularTracks should return popular tracks for a given artist`() {
        // Mocking repository response
        val albums = listOf(
            AlbumModel2("album-id","","AlbumName","AlbumArtist","","", listOf(TrackModel2("trackId", "TrackName", "ArtistName", "features",12,"trackUrl", "coverUrl")))
        )
        Mockito.`when`(repository.getAllAlbums()).thenReturn(Flux.fromIterable(albums))

        // Act
        val result = service.getArtistPopularTracks("ArtistName")

        // Assert
        StepVerifier.create(result)
            .assertNext { tracks ->
                Assertions.assertEquals(tracks.tracks?.size, 1)
                Assertions.assertEquals(tracks.tracks?.get(0)?.id, "trackId")
            }
            .verifyComplete()
    }

    @Test
    fun `trackPlayed should update track played count when track exists`() {
        // Mocking repository response
        val albums = listOf(
            AlbumModel2("album-id","","AlbumName","AlbumArtist","","", listOf(TrackModel2("trackId", "TrackName", "ArtistName", "features",12,"trackUrl", "coverUrl")))
        )

        Mockito.`when`(repository.getAllAlbums()).thenReturn(Flux.fromIterable(albums))
        Mockito.`when`(repository.updateTrackPlayed("album-id", "trackId")).thenReturn(Mono.empty())

        // Act
        val result = service.trackPlayed("trackId")

        // Assert
        StepVerifier.create(result)
            .expectComplete()
            .verify()
    }

    @Test
    fun `trackPlayed should throw error when track does not exist`() {
        // Mocking repository response
        val albums = listOf(
            AlbumModel2("album-id","","AlbumName","AlbumArtist","","", listOf(TrackModel2("trackId", "TrackName", "ArtistName", "features",12,"trackUrl", "coverUrl")))
        )
        Mockito.`when`(repository.getAllAlbums()).thenReturn(Flux.fromIterable(albums))

        // Act
        val result = service.trackPlayed("nonExistentTrackId")

        // Assert
        StepVerifier.create(result)
            .expectError(KoshaGatewayException::class.java)
            .verify()
    }
}