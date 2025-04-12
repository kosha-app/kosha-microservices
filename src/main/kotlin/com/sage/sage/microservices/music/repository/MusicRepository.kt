package com.sage.sage.microservices.music.repository

import com.sage.sage.microservices.music.model.request.AlbumModel2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MusicRepository: JpaRepository<AlbumModel2, String> {
}