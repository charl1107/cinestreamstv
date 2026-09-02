package com.cinestreamtv.core.domain.usecase

import com.cinestreamtv.core.domain.model.MediaDetails
import com.cinestreamtv.core.domain.repository.ContentRepository
import javax.inject.Inject

class GetMediaDetailsUseCase @Inject constructor(
    private val contentRepository: ContentRepository
) {
    suspend operator fun invoke(
        url: String,
        providerName: String
    ): Result<MediaDetails> = runCatching {
        contentRepository.getDetails(url, providerName)
    }
}
