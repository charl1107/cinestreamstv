package com.cinestreamtv.core.data.mapper

import com.cinestreamtv.core.data.local.entity.BookmarkEntity
import com.cinestreamtv.core.data.local.entity.WatchHistoryEntity
import com.cinestreamtv.core.domain.model.*

fun WatchHistoryEntity.toDomain(): WatchHistoryItem = WatchHistoryItem(
    mediaId = mediaId,
    title = title,
    posterUrl = posterUrl,
    type = MediaType.valueOf(type),
    providerName = providerName,
    lastPosition = lastPosition,
    totalDuration = totalDuration,
    lastWatched = lastWatched,
    episodeInfo = episodeInfo
)

fun WatchHistoryItem.toEntity(): WatchHistoryEntity = WatchHistoryEntity(
    mediaId = mediaId,
    title = title,
    posterUrl = posterUrl,
    type = type.name,
    providerName = providerName,
    lastPosition = lastPosition,
    totalDuration = totalDuration,
    lastWatched = lastWatched,
    episodeInfo = episodeInfo
)

fun BookmarkEntity.toDomain(): BookmarkItem = BookmarkItem(
    mediaId = mediaId,
    title = title,
    posterUrl = posterUrl,
    type = MediaType.valueOf(type),
    providerName = providerName,
    url = url,
    addedAt = addedAt
)

fun BookmarkItem.toEntity(): BookmarkEntity = BookmarkEntity(
    mediaId = mediaId,
    title = title,
    posterUrl = posterUrl,
    type = type.name,
    providerName = providerName,
    url = url,
    addedAt = addedAt
)
