package com.unica.bxhbaihatapi.model.song

import com.unica.bxhbaihatapi.db.entity.SongSearch

data class SongSearchResponse(
    val data: MutableList<Data>,
    val result: Boolean
)

data class Data(
    val song: MutableList<SongSearch>
)
