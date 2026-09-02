package com.cinestreamtv.core.domain.model

data class HomePageData(
    val heroItems: List<MediaItem> = emptyList(),
    val rows: List<HomePageRow> = emptyList()
)

data class HomePageRow(
    val title: String,
    val items: List<MediaItem>,
    val providerName: String? = null
)
