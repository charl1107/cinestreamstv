package com.cinestreamtv.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val mediaId: String,
    val title: String,
    val posterUrl: String?,
    val type: String,
    val providerName: String,
    val lastPosition: Long,
    val totalDuration: Long,
    val lastWatched: Long,
    val episodeInfo: String? = null
)
