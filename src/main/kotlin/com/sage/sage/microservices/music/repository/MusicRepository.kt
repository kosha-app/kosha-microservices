package com.sage.sage.microservices.music.repository

import com.azure.cosmos.models.CosmosItemRequestOptions
import com.azure.cosmos.models.PartitionKey
import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.music.repository.MusicDatabaseConstants.DATABASE_ALBUMS_COLLECTION
import com.sage.sage.microservices.music.repository.MusicDatabaseConstants.DATABASE_TRACKS_COLLECTION
import com.sage.sage.microservices.music.model.request.AlbumResponse
import com.sage.sage.microservices.music.model.request.AlbumResponse.Companion.toAlbumModel
import com.sage.sage.microservices.music.model.request.TrackResponse
import com.sage.sage.microservices.music.model.request.TrackResponse.Companion.toTrackModel
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.firebase.cloud.FirestoreClient
import com.sage.sage.microservices.azure.AzureInitializer
import com.sage.sage.microservices.music.model.request.AlbumModel
import com.sage.sage.microservices.music.model.request.AlbumModel2
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class MusicRepository(
    private val azureInitializer: AzureInitializer
): IMusicRepository {

    val albumKey = "album"
    override fun getTrack(trackID: String): TrackResponse {
        val database = FirestoreClient.getFirestore()
        val document: ApiFuture<DocumentSnapshot> =
            database.collection(DATABASE_TRACKS_COLLECTION).document(trackID).get()
        val documentSnapshot = document.get()

        return documentSnapshot.toTrackModel()
    }

    override fun createAlbum(albumRequest: AlbumModel): Int? {
        albumRequest.albumKey = albumKey
        albumRequest.id = UUID.randomUUID().toString()
        val response = azureInitializer.albumContainer?.createItem(
            albumRequest,
            PartitionKey(albumRequest.albumKey),
            CosmosItemRequestOptions()
        )
        return response?.statusCode
    }

    override fun getAlbum(albumId: String): AlbumModel? {
        val response = azureInitializer.albumContainer?.readItem(
            albumId,
            PartitionKey(albumKey),
            AlbumModel::class.java
        )

        return response?.item
    }

    override fun getAllAlbums(): List<AlbumModel2>? {
        val response = azureInitializer.albumContainer?.readAllItems(
            PartitionKey(albumKey),
            AlbumModel2::class.java
        )

        return response?.toList()
    }


}