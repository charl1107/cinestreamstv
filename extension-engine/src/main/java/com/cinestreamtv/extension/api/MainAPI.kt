package com.cinestreamtv.extension.api

abstract class MainAPI {
    abstract val name: String
    abstract val mainUrl: String
    open val lang: String = "en"
    
    open val supportedTypes: Set<TvType> = setOf(
        TvType.Movie, TvType.TvSeries
    )
    
    open val hasMainPage: Boolean = false
    open val hasQuickSearch: Boolean = false
    open val hasDownloadSupport: Boolean = true
    
    open val mainPage: List<MainPageRequest> = emptyList()
    
    open val vpnStatus: VPNStatus = VPNStatus.None
    
    open suspend fun getMainPage(
        page: Int = 1,
        request: MainPageRequest
    ): HomePageResponse? = null
    
    open suspend fun search(query: String): List<SearchResponse>? = null
    open suspend fun quickSearch(query: String): List<SearchResponse>? = search(query)
    
    open suspend fun load(url: String): LoadResponse? = null
    
    open suspend fun loadLinks(
        data: String,
        isCasting: Boolean = false,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean = false
    
    enum class VPNStatus {
        None,
        MightBeNeeded,
        Torrent
    }
}
