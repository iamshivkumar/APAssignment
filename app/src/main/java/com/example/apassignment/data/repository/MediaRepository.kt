package com.example.apassignment.data.repository

import com.example.apassignment.data.remote.MediaApi
import com.example.apassignment.domain.model.MediaItem

class MediaRepository(
    private val api: MediaApi
) {

    suspend fun loadPage(
        page: Int,
        pageSize: Int
    ): List<MediaItem> {
        val offset = page * pageSize
        return api.fetchMedia(pageSize, offset)
    }
}
