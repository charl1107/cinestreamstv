package com.cinestreamtv.core.domain.repository

import com.cinestreamtv.core.domain.model.*
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    fun getWatchHistory(): Flow<List<WatchHistoryItem>>
    suspend fun saveWatchProgress(item: WatchHistoryItem)
    suspend fun deleteWatchHistory(mediaId: String)
    suspend fun clearWatchHistory()
    
    fun getBookmarks(): Flow<List<BookmarkItem>>
    suspend fun addBookmark(item: BookmarkItem)
    suspend fun removeBookmark(mediaId: String)
    suspend fun isBookmarked(mediaId: String): Boolean
}
