package com.unica.bxhbaihatapi.model.song

import com.google.gson.annotations.SerializedName

data class ZingResponse(
    var err: Int = 0,
    var msg: String = "",
    var data: ZingData? = null
) {

}

data class ZingData(
    var song: MutableList<Song>
)

data class Song(
    var id: String = "",
    var name: String = "",
    var title: String = "",
    var code: String = "",
    var duration: Int = 0,
    var total: Int = 0,
    var artist: Artist? = null,
    @SerializedName("thumbnail")
    var thumbNail: String? = null,
    var album: Album? = null
)

data class Album(
    @SerializedName("thumbnail_medium")
    var thumbnailMedium: String = "",
    val artists :MutableList<Artist> = mutableListOf<Artist>()
)

data class Artist(
    var id: String = "",
    var name: String = "",
    var thumbnail: String = ""

)