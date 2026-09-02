package com.cinestreamtv.core.domain.usecase

import com.cinestreamtv.core.domain.model.BookmarkItem
import com.cinestreamtv.core.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageBookmarksUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    fun getBookmarks(): Flow<List<BookmarkItem>> = userDataRepository.getBookmarks()
    suspend fun addBookmark(item: BookmarkItem) = userDataRepository.addBookmark(item)
    suspend fun removeBookmark(mediaId: String) = userDataRepository.removeBookmark(mediaId)
    suspend fun isBookmarked(mediaId: String): Boolean = userDataRepository.isBookmarked(mediaId)
}
