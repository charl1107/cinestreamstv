package com.cinestreamtv.extension.api

open class LoadResponse(
    open val name: String,
    open val url: String,
    open val apiName: String,
    open val type: TvType,
    open val posterUrl: String? = null,
    open val year: Int? = null,
    open val plot: String? = null,
    open val rating: Int? = null,
    open val tags: List<String>? = null,
    open val duration: Int? = null,
    open val trailerUrl: String? = null,
    open val recommendations: List<SearchResponse>? = null,
    open val actors: List<ActorInfo>? = null,
    open val backgroundPosterUrl: String? = null
)

data class ActorInfo(
    val name: String,
    val image: String? = null,
    val roleString: String? = null
)

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
    name = name,
    url = url,
    apiName = apiName,
    type = TvType.Movie,
    posterUrl = posterUrl,
    year = year,
    plot = plot,
    rating = rating,
    tags = tags,
    duration = duration,
    trailerUrl = trailerUrl,
    recommendations = recommendations,
    actors = actors,
    backgroundPosterUrl = backgroundPosterUrl
)

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
    name = name,
    url = url,
    apiName = apiName,
    type = TvType.TvSeries,
    posterUrl = posterUrl,
    year = year,
    plot = plot,
    rating = rating,
    tags = tags,
    duration = duration,
    trailerUrl = trailerUrl,
    recommendations = recommendations,
    actors = actors,
    backgroundPosterUrl = backgroundPosterUrl
)

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

data class SeasonData(
    val season: Int,
    val name: String? = null,
    val displaySeason: Int? = null
)
