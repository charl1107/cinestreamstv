package com.cinestreamtv.core.domain.repository

import com.cinestreamtv.core.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    suspend fun search(query: String, providerNames: List<String>? = null): List<MediaItem>
    suspend fun getHomePage(providerNames: List<String>? = null): HomePageData
    suspend fun getDetails(url: String, providerName: String): MediaDetails
    suspend fun getStreamLinks(data: String, providerName: String): List<StreamLink>
}
