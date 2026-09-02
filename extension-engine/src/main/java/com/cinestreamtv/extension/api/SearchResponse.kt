package com.cinestreamtv.extension.api

import kotlinx.serialization.Serializable

@Serializable
open class SearchResponse(
    val name: String,
    val url: String,
    val apiName: String,
    val type: TvType? = null,
    val posterUrl: String? = null,
    val year: Int? = null,
    val quality: SearchQuality? = null,
    val posterHeaders: Map<String, String>? = null
)

@Serializable
enum class SearchQuality {
    Cam, CamRip, HdCam,
    Telecine, Telesync,
    Dvd, DvdRip, DvdScr,
    HD, SD,
    BlueRay, WebRip,
    FourK, UHD
}
