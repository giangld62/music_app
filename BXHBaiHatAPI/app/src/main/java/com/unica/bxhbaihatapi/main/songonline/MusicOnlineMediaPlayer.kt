package com.unica.bxhbaihatapi.main.songonline

import android.media.MediaPlayer
import android.util.Log

class MusicOnlineMediaPlayer : MediaPlayer(),MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {
    private var mp : MediaPlayer? = null
    override fun setDataSource(path:String){
        release()
        mp = MediaPlayer()
        mp?.setOnErrorListener(this)
        mp?.setOnCompletionListener(this)
        mp?.setDataSource(path)
        mp?.setOnPreparedListener(this)
        mp?.setOnBufferingUpdateListener(this)
        mp?.prepareAsync()
    }

    override fun release() {
        mp?.release()
    }

    override fun pause(){
        mp?.pause()
    }

    override fun start(){
        mp?.start()
    }

    override fun stop(){
        mp?.stop()
    }


    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return true
    }

    override fun onCompletion(mp: MediaPlayer?) {

    }

    override fun onPrepared(mp: MediaPlayer?) {
        start()
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        Log.d("MusicOnlineMediaPlayer", "onBufferingUpdate percent: $percent")
    }
}