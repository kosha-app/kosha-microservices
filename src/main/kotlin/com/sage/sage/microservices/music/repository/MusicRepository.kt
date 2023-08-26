package com.sage.sage.microservices.music.repository

import com.sage.sage.microservices.music.repository.MusicDatabaseConstants.DATABASE_ALBUMS_COLLECTION
import com.sage.sage.microservices.music.repository.MusicDatabaseConstants.DATABASE_TRACKS_COLLECTION
import com.sage.sage.microservices.music.model.request.AlbumResponse
import com.sage.sage.microservices.music.model.request.AlbumResponse.Companion.toAlbumModel
import com.sage.sage.microservices.music.model.request.TrackResponse
import com.sage.sage.microservices.music.model.request.TrackResponse.Companion.toTrackModel
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Repository

@Repository
class MusicRepository: IMusicRepository {
    override fun getTrack(trackID: String): TrackResponse {
        val database = FirestoreClient.getFirestore()
        val document: ApiFuture<DocumentSnapshot> =
            database.collection(DATABASE_TRACKS_COLLECTION).document(trackID).get()
        val documentSnapshot = document.get()

        return documentSnapshot.toTrackModel()
    }

    override fun getAlbum(albumId: String): AlbumResponse {
        val database = FirestoreClient.getFirestore()
        val document: ApiFuture<DocumentSnapshot> =
            database.collection(DATABASE_ALBUMS_COLLECTION).document(albumId).get()
        val documentSnapshot = document.get()

        return documentSnapshot.toAlbumModel()
    }
}