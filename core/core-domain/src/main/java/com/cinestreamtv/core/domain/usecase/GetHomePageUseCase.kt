package com.cinestreamtv.core.domain.usecase

import com.cinestreamtv.core.domain.model.HomePageData
import com.cinestreamtv.core.domain.repository.ContentRepository
import javax.inject.Inject

class GetHomePageUseCase @Inject constructor(
    private val contentRepository: ContentRepository
) {
    suspend operator fun invoke(
        providerNames: List<String>? = null
    ): Result<HomePageData> = runCatching {
        contentRepository.getHomePage(providerNames)
    }
}
