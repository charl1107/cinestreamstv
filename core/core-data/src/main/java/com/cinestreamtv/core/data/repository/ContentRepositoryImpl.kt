package com.cinestreamtv.core.data.repository

import com.cinestreamtv.core.domain.model.*
import com.cinestreamtv.core.domain.repository.ContentRepository
import com.cinestreamtv.extension.ExtensionManager
import com.cinestreamtv.extension.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val extensionManager: ExtensionManager
) : ContentRepository {

    override suspend fun search(query: String, providerNames: List<String>?): List<MediaItem> {
        val providers = if (providerNames != null) {
            providerNames.mapNotNull { extensionManager.getProvider(it) }
        } else {
            extensionManager.getInstalledProviders()
        }
        return providers.flatMap { provider ->
            try {
                provider.search(query)?.map { it.toMediaItem(provider.name) } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getHomePage(providerNames: List<String>?): HomePageData {
        val providers = if (providerNames != null) {
            providerNames.mapNotNull { extensionManager.getProvider(it) }
        } else {
            extensionManager.getInstalledProviders().filter { it.hasMainPage }
        }
        val allRows = mutableListOf<HomePageRow>()
        val heroItems = mutableListOf<MediaItem>()

        for (provider in providers) {
            try {
                for (request in provider.mainPage) {
                    val response = provider.getMainPage(page = 1, request = request) ?: continue
                    for (list in response.items) {
                        val items = list.list.map { it.toMediaItem(provider.name) }
                        allRows.add(HomePageRow(
                            title = list.name,
                            items = items,
                            providerName = provider.name
                        ))
                        if (heroItems.size < 5 && items.isNotEmpty()) {
                            heroItems.addAll(items.take(5 - heroItems.size))
                        }
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }

        return HomePageData(heroItems = heroItems, rows = allRows)
    }

    override suspend fun getDetails(url: String, providerName: String): MediaDetails {
        val provider = extensionManager.getProvider(providerName)
            ?: throw Exception("Provider $providerName not found")
        val response = provider.load(url)
            ?: throw Exception("Failed to load details from $providerName")
        return response.toMediaDetails(providerName)
    }

    override suspend fun getStreamLinks(data: String, providerName: String): List<StreamLink> {
        val provider = extensionManager.getProvider(providerName)
            ?: throw Exception("Provider $providerName not found")
        val links = mutableListOf<StreamLink>()
        provider.loadLinks(
            data = data,
            subtitleCallback = { subtitle ->
                // Subtitles are attached to links
            },
            callback = { link ->
                links.add(link.toStreamLink())
            }
        )
        return links
    }

    private fun SearchResponse.toMediaItem(providerName: String): MediaItem = MediaItem(
        id = "${providerName}_${url.hashCode()}",
        title = name,
        type = when (type) {
            TvType.Movie, TvType.AnimeMovie -> MediaType.MOVIE
            TvType.TvSeries, TvType.Asian -> MediaType.SERIES
            TvType.Anime, TvType.OVA, TvType.Cartoon -> MediaType.ANIME
            TvType.Live -> MediaType.LIVE
            TvType.Documentary -> MediaType.DOCUMENTARY
            else -> MediaType.MOVIE
        },
        posterUrl = posterUrl,
        year = year,
        providerName = providerName,
        url = url
    )

    private fun LoadResponse.toMediaDetails(providerName: String): MediaDetails {
        val mediaItem = MediaItem(
            id = "${providerName}_${url.hashCode()}",
            title = name,
            type = when (type) {
                TvType.Movie, TvType.AnimeMovie -> MediaType.MOVIE
                TvType.TvSeries, TvType.Asian -> MediaType.SERIES
                TvType.Anime, TvType.OVA, TvType.Cartoon -> MediaType.ANIME
                TvType.Live -> MediaType.LIVE
                TvType.Documentary -> MediaType.DOCUMENTARY
                else -> MediaType.MOVIE
            },
            posterUrl = posterUrl,
            backdropUrl = backgroundPosterUrl,
            year = year,
            rating = rating?.toDouble()?.div(10),
            overview = plot,
            providerName = providerName,
            url = url
        )

        val seasons = if (this is TvSeriesLoadResponse) {
            val episodesBySeason = episodes.groupBy { it.season ?: 1 }
            episodesBySeason.map { (seasonNum, eps) ->
                Season(
                    seasonNumber = seasonNum,
                    name = seasonNames?.find { it.season == seasonNum }?.name,
                    episodes = eps.map { ep ->
                        Episode(
                            season = ep.season ?: 1,
                            episode = ep.episode ?: 0,
                            title = ep.name,
                            description = ep.description,
                            thumbnailUrl = ep.posterUrl,
                            data = ep.data,
                            rating = ep.rating?.toDouble()?.div(10)
                        )
                    }
                )
            }
        } else emptyList()

        val dataStr = if (this is MovieLoadResponse) dataUrl else ""

        return MediaDetails(
            item = mediaItem,
            plot = plot,
            genres = tags ?: emptyList(),
            actors = actors?.map { ActorData(name = it.name, role = it.roleString, imageUrl = it.image) } ?: emptyList(),
            duration = duration,
            seasons = seasons,
            recommendations = recommendations?.map { it.toMediaItem(providerName) } ?: emptyList(),
            trailerUrl = trailerUrl,
            data = dataStr
        )
    }

    private fun ExtractorLink.toStreamLink(): StreamLink = StreamLink(
        url = url,
        name = "$source - $name",
        quality = when {
            quality >= Qualities.P2160.value -> Quality.UHD
            quality >= Qualities.P1080.value -> Quality.FHD
            quality >= Qualities.P720.value -> Quality.HD
            quality > 0 -> Quality.SD
            else -> Quality.UNKNOWN
        },
        headers = headers,
        isM3U8 = type == ExtractorLinkType.M3U8,
        isDash = type == ExtractorLinkType.DASH,
        referer = referer
    )
}
