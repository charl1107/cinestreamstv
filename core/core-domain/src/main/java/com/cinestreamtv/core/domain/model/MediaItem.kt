package com.cinestreamtv.core.domain.model

enum class MediaType { MOVIE, SERIES, ANIME, LIVE, DOCUMENTARY }

enum class Quality { SD, HD, FHD, UHD, UNKNOWN }

data class MediaItem(
    val id: String,
    val title: String,
    val type: MediaType,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val year: Int? = null,
    val rating: Double? = null,
    val overview: String? = null,
    val providerName: String,
    val url: String
)
