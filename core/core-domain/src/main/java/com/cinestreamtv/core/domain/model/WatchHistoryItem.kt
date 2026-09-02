package com.cinestreamtv.core.domain.model

data class WatchHistoryItem(
    val mediaId: String,
    val title: String,
    val posterUrl: String?,
    val type: MediaType,
    val providerName: String,
    val lastPosition: Long,
    val totalDuration: Long,
    val lastWatched: Long,
    val episodeInfo: String? = null
)

data class BookmarkItem(
    val mediaId: String,
    val title: String,
    val posterUrl: String?,
    val type: MediaType,
    val providerName: String,
    val url: String,
    val addedAt: Long
)
