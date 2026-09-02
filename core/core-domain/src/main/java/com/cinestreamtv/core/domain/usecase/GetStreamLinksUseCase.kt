package com.cinestreamtv.core.domain.usecase

import com.cinestreamtv.core.domain.model.StreamLink
import com.cinestreamtv.core.domain.repository.ContentRepository
import javax.inject.Inject

class GetStreamLinksUseCase @Inject constructor(
    private val contentRepository: ContentRepository
) {
    suspend operator fun invoke(
        data: String,
        providerName: String
    ): Result<List<StreamLink>> = runCatching {
        contentRepository.getStreamLinks(data, providerName)
    }
}
