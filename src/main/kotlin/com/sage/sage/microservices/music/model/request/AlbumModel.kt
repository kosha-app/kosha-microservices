package com.sage.sage.microservices.music.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.music.model.request.TrackResponse.Companion.toTrackModel
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import jakarta.persistence.*
import reactor.core.publisher.Mono

data class AlbumAddRequest(
    @JsonProperty("id") var id: String,
    @JsonProperty("albumName") val albumName: String,
    @JsonProperty("albumArtist") val albumArtist: String,
    @JsonProperty("releaseDate") val releaseDate: String,
    @JsonProperty("coverUrl") val coverUrl: String,
    @JsonProperty("tracks") val tracks: List<TrackModel>
)


data class AlbumModel(
    @JsonProperty("id") var id: String,
    @JsonProperty("albumName") val albumName: String,
    @JsonProperty("albumArtist") val albumArtist: String,
    @JsonProperty("releaseDate") val releaseDate: String,
    @JsonProperty("coverUrl") val coverUrl: String,
    @JsonProperty("tracks") val tracks: List<TrackModel>
)

@Entity
@Table(name="album")
data class AlbumModel2(
    @Id var id: String,
    val albumName: String,
    val albumArtist: String,
    val releaseDate: String,
    @Column(columnDefinition = "TEXT")
    val coverUrl: String,
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "albumId", referencedColumnName = "id")
    val tracks: List<TrackModel2>
){
    companion object{
        /**
         * Wraps this AlbumModel2 instance in a Reactor Mono.
         *
         * @return a Mono emitting this AlbumModel2 instance.
         */
        fun AlbumModel2.toMono(): Mono<AlbumModel2>{
            return Mono.just(
                AlbumModel2(
                    id, albumName, albumArtist, releaseDate, coverUrl, tracks
                )
            )
        }
    }
}

data class TrackPlayedRequest(
    @JsonProperty("albumId") var albumId: String,
    @JsonProperty("trackId") var trackId: String
)