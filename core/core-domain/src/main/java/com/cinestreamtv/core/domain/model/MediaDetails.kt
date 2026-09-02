package com.cinestreamtv.core.domain.model

data class MediaDetails(
    val item: MediaItem,
    val plot: String? = null,
    val genres: List<String> = emptyList(),
    val actors: List<ActorData> = emptyList(),
    val duration: Int? = null,
    val seasons: List<Season> = emptyList(),
    val recommendations: List<MediaItem> = emptyList(),
    val trailerUrl: String? = null,
    val data: String = ""
)

data class ActorData(
    val name: String,
    val role: String? = null,
    val imageUrl: String? = null
)
