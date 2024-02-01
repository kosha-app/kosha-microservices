package com.sage.sage.microservices.music.repository

import com.azure.cosmos.models.CosmosItemRequestOptions
import com.azure.cosmos.models.PartitionKey
import com.sage.sage.microservices.config.azure.AzureInitializer
import com.sage.sage.microservices.music.model.request.AlbumModel
import com.sage.sage.microservices.music.model.request.AlbumModel2
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class MusicRepository(
    private val azureInitializer: AzureInitializer
): IMusicRepository {

    val albumKey = "album"

    override fun createAlbum(albumRequest: AlbumModel): Mono<Void> {
        albumRequest.albumKey = albumKey
        albumRequest.id = UUID.randomUUID().toString()
        azureInitializer.albumContainer?.createItem(
            albumRequest,
            PartitionKey(albumRequest.albumKey),
            CosmosItemRequestOptions()
        )
        return Mono.empty()
    }

    override fun getAlbum(albumId: String): Mono<AlbumModel> {
        val response = azureInitializer.albumContainer?.readItem(
            albumId,
            PartitionKey(albumKey),
            AlbumModel::class.java
        )

        return Mono.justOrEmpty(response?.item)
    }

    override fun getAllAlbums(): Flux<AlbumModel2> {
        val response = azureInitializer.albumContainer?.readAllItems(
            PartitionKey(albumKey),
            AlbumModel2::class.java
        )

        return if (response != null) {
            Flux.fromIterable(response.toList())
        }else {
            Flux.empty()
        }
    }

}