package com.cinestreamtv.core.data.repository

import com.cinestreamtv.core.data.local.dao.BookmarkDao
import com.cinestreamtv.core.data.local.dao.WatchHistoryDao
import com.cinestreamtv.core.data.mapper.toDomain
import com.cinestreamtv.core.data.mapper.toEntity
import com.cinestreamtv.core.domain.model.BookmarkItem
import com.cinestreamtv.core.domain.model.WatchHistoryItem
import com.cinestreamtv.core.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepositoryImpl @Inject constructor(
    private val watchHistoryDao: WatchHistoryDao,
    private val bookmarkDao: BookmarkDao
) : UserDataRepository {

    override fun getWatchHistory(): Flow<List<WatchHistoryItem>> =
        watchHistoryDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveWatchProgress(item: WatchHistoryItem) =
        watchHistoryDao.insert(item.toEntity())

    override suspend fun deleteWatchHistory(mediaId: String) =
        watchHistoryDao.delete(mediaId)

    override suspend fun clearWatchHistory() =
        watchHistoryDao.deleteAll()

    override fun getBookmarks(): Flow<List<BookmarkItem>> =
        bookmarkDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addBookmark(item: BookmarkItem) =
        bookmarkDao.insert(item.toEntity())

    override suspend fun removeBookmark(mediaId: String) =
        bookmarkDao.delete(mediaId)

    override suspend fun isBookmarked(mediaId: String): Boolean =
        bookmarkDao.exists(mediaId)
}
