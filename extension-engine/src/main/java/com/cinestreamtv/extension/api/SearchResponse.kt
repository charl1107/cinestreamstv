package com.cinestreamtv.extension.api

open class SearchResponse(
    open val name: String,
    open val url: String,
    open val apiName: String,
    open val type: TvType? = null,
    open val posterUrl: String? = null,
    open val year: Int? = null,
    open val quality: SearchQuality? = null,
    open val posterHeaders: Map<String, String>? = null
)

enum class SearchQuality {
    Cam, CamRip, HdCam,
    Telecine, Telesync,
    Dvd, DvdRip, DvdScr,
    HD, SD,
    BlueRay, WebRip,
    FourK, UHD
}
