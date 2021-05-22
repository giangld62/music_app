package com.unica.bxhbaihatapi.model

import com.unica.bxhbaihatapi.model.song.SongSearchResponse
import com.unica.bxhbaihatapi.model.song.ZingResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

open interface ZingApi {
    @GET("/xhr/chart-realtime")
    fun getAllSong(
        @Query("songId") songId: Int = 0,
        @Query("videoId") videoId: Int = 0,
        @Query("albumId") albumId: Int = 0,
        @Query("chart") chart: String = "song",
        @Query("time") time: Int = -1
    ): Observable<ZingResponse>

    @GET("/complete")
    fun getSong(
        @Query("type") type: String = "artist,song,key,code",
        @Query("num") num: Int = 500,
        @Query("query") songName: String
    ): Observable<SongSearchResponse>


}