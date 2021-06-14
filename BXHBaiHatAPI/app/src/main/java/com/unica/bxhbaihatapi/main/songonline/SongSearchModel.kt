package com.unica.bxhbaihatapi.main.songonline

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.unica.bxhbaihatapi.common.MyApp
import com.unica.bxhbaihatapi.db.entity.SongSearch
import com.unica.bxhbaihatapi.model.ZingApi
import com.unica.bxhbaihatapi.model.ZingApiUtil
import com.unica.bxhbaihatapi.model.song.Song
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SongSearchModel {
    val songRes = MutableLiveData<MutableList<Song>>()
    val songSearchRes = MutableLiveData<MutableList<SongSearch>>()
    private val zingApi: ZingApi
    private val zingApiSearch: ZingApi

    constructor() {
        zingApi = ZingApiUtil.createZingApi()
        zingApiSearch = ZingApiUtil.createZingApi(
            "http://ac.mp3.zing.vn"
        )
    }

    @SuppressLint("CheckResult")
    fun getSongFavourite() {
        zingApi.getAllSong()
            //tuong tac voi internal nam tren thread nao
            .subscribeOn(Schedulers.newThread())
            //du lieu thanh cong se tra ve thread nao
            .observeOn(AndroidSchedulers.mainThread())
            //bat dau call
            .subscribe(
                {
                    songRes.value = it.data!!.song
                },
                {
                    print("")

                }
            )

    }

    @SuppressLint("CheckResult")
    fun getSong(songName: String) {
        zingApiSearch.getSong(songName = songName)
            //tuong tac voi internal nam tren thread nao
            .subscribeOn(Schedulers.newThread())
            //du lieu thanh cong se tra ve thread nao
            .observeOn(AndroidSchedulers.mainThread())
            //bat dau call
            .subscribe(
                {
                    val songs = it.data[0].song
                    saveSongSearch(songs)
                    songSearchRes.value = songs
                },
                {
                    print("")
                    val songs = MyApp.getDB().songSearchDao().getByName(songName)
                    songSearchRes.value = songs
                }
            )

    }

    @SuppressLint("CheckResult")
    private fun saveSongSearch(songs: MutableList<SongSearch>) {
        Observable.create<MutableList<SongSearch>> {
            MyApp.getDB().songSearchDao().insertAll(songs)
            it.onNext(songs)
            it.onComplete()
        }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                MyApp.getDB().songSearchDao().insertAll(songs)
            }, {
                it.printStackTrace()
            })
    }
}