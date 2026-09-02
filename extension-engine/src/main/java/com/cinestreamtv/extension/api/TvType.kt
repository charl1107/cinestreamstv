package com.cinestreamtv.extension.api

enum class TvType {
    Movie,
    TvSeries,
    Anime,
    AnimeMovie,
    OVA,
    Cartoon,
    Asian,
    Live,
    NSFW,
    Documentary,
    Others;

    companion object {
        fun fromString(type: String): TvType {
            return entries.firstOrNull { it.name.equals(type, ignoreCase = true) } ?: Others
        }
    }
}
