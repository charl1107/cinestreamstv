package com.cinestreamtv.extension.api

data class ExtractorLink(
    val source: String,
    val name: String,
    val url: String,
    val referer: String,
    val quality: Int = Qualities.Unknown.value,
    val type: ExtractorLinkType = ExtractorLinkType.VIDEO,
    val headers: Map<String, String> = emptyMap(),
    val extractorData: String? = null
)

enum class ExtractorLinkType {
    VIDEO,
    M3U8,
    DASH,
    MAGNET
}

enum class Qualities(val value: Int) {
    Unknown(0),
    P360(360),
    P480(480),
    P720(720),
    P1080(1080),
    P1440(1440),
    P2160(2160)
}

data class SubtitleFile(
    val lang: String,
    val url: String,
    val headers: Map<String, String> = emptyMap()
)
