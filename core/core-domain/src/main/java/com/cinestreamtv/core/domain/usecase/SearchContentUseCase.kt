package com.cinestreamtv.core.domain.usecase

import com.cinestreamtv.core.domain.model.MediaItem
import com.cinestreamtv.core.domain.repository.ContentRepository
import javax.inject.Inject

class SearchContentUseCase @Inject constructor(
    private val contentRepository: ContentRepository
) {
    suspend operator fun invoke(
        query: String,
        providerNames: List<String>? = null
    ): Result<List<MediaItem>> = runCatching {
        contentRepository.search(query, providerNames)
    }
}
