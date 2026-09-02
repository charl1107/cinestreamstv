package com.cinestreamtv.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Episode(
    val season: Int,
    val episode: Int,
    val title: String? = null,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val data: String = "",
    val rating: Double? = null,
    val airDate: String? = null
)

@Serializable
data class Season(
    val seasonNumber: Int,
    val name: String? = null,
    val episodes: List<Episode> = emptyList()
)
