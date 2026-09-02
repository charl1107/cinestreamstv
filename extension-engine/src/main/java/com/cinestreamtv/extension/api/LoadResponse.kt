package com.cinestreamtv.extension.api

import kotlinx.serialization.Serializable

@Serializable
open class LoadResponse(
    val name: String,
    val url: String,
    val apiName: String,
    val type: TvType,
    val posterUrl: String? = null,
    val year: Int? = null,
    val plot: String? = null,
    val rating: Int? = null,
    val tags: List<String>? = null,
    val duration: Int? = null,
    val trailerUrl: String? = null,
    val recommendations: List<SearchResponse>? = null,
    val actors: List<ActorInfo>? = null,
    val backgroundPosterUrl: String? = null
)

@Serializable
data class ActorInfo(
    val name: String,
    val image: String? = null,
    val roleString: String? = null
)

@Serializable
class MovieLoadResponse(
    name: String,
    url: String,
    apiName: String,
    val dataUrl: String,
    posterUrl: String? = null,
    year: Int? = null,
    plot: String? = null,
    rating: Int? = null,
    tags: List<String>? = null,
    duration: Int? = null,
    trailerUrl: String? = null,
    recommendations: List<SearchResponse>? = null,
    actors: List<ActorInfo>? = null,
    backgroundPosterUrl: String? = null
) : LoadResponse(
    name, url, apiName, TvType.Movie, posterUrl, year, plot,
    rating, tags, duration, trailerUrl, recommendations, actors, backgroundPosterUrl
)

@Serializable
class TvSeriesLoadResponse(
    name: String,
    url: String,
    apiName: String,
    val episodes: List<TvSeriesEpisode>,
    posterUrl: String? = null,
    year: Int? = null,
    plot: String? = null,
    rating: Int? = null,
    tags: List<String>? = null,
    duration: Int? = null,
    trailerUrl: String? = null,
    recommendations: List<SearchResponse>? = null,
    actors: List<ActorInfo>? = null,
    backgroundPosterUrl: String? = null,
    val seasonNames: List<SeasonData>? = null
) : LoadResponse(
    name, url, apiName, TvType.TvSeries, posterUrl, year, plot,
    rating, tags, duration, trailerUrl, recommendations, actors, backgroundPosterUrl
)

@Serializable
data class TvSeriesEpisode(
    val name: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val data: String,
    val posterUrl: String? = null,
    val rating: Int? = null,
    val description: String? = null,
    val date: String? = null
)

@Serializable
data class SeasonData(
    val season: Int,
    val name: String? = null,
    val displaySeason: Int? = null
)
