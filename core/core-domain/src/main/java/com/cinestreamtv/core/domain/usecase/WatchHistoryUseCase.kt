package com.cinestreamtv.core.domain.usecase

import com.cinestreamtv.core.domain.model.WatchHistoryItem
import com.cinestreamtv.core.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WatchHistoryUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository
) {
    fun getWatchHistory(): Flow<List<WatchHistoryItem>> = userDataRepository.getWatchHistory()
    suspend fun saveProgress(item: WatchHistoryItem) = userDataRepository.saveWatchProgress(item)
    suspend fun delete(mediaId: String) = userDataRepository.deleteWatchHistory(mediaId)
    suspend fun clearAll() = userDataRepository.clearWatchHistory()
}
