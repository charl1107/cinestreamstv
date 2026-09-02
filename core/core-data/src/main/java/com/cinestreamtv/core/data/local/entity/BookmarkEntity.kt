package com.cinestreamtv.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val mediaId: String,
    val title: String,
    val posterUrl: String?,
    val type: String,
    val providerName: String,
    val url: String,
    val addedAt: Long
)
