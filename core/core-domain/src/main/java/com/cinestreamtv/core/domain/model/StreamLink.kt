package com.cinestreamtv.core.domain.model

data class StreamLink(
    val url: String,
    val name: String,
    val quality: Quality = Quality.UNKNOWN,
    val headers: Map<String, String> = emptyMap(),
    val subtitles: List<SubtitleTrack> = emptyList(),
    val isM3U8: Boolean = false,
    val isDash: Boolean = false,
    val referer: String? = null
)

data class SubtitleTrack(
    val url: String,
    val language: String,
    val label: String? = null,
    val mimeType: String? = null
)

data class AudioTrack(
    val id: String,
    val language: String,
    val label: String? = null
)
