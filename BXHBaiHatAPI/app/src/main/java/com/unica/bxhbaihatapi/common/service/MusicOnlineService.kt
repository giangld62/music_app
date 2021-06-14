package com.unica.bxhbaihatapi.common.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.core.app.NotificationCompat
import com.unica.bxhbaihatapi.R
import com.unica.bxhbaihatapi.common.MyApp
import com.unica.bxhbaihatapi.common.broadcastreceiver.MusicOnlineReceiver
import com.unica.bxhbaihatapi.common.ActionMusic
import com.unica.bxhbaihatapi.db.entity.SongSearch
import com.unica.bxhbaihatapi.main.songonline.PlayerActivity
import com.unica.bxhbaihatapi.main.songonline.SongSearchFragment
import com.unica.bxhbaihatapi.model.song.Song
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MusicOnlineService : Service(), MediaPlayer.OnCompletionListener {
    var mBinder: IBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private var favoriteSongList: MutableList<Song>? = null
    private var searchSongList: MutableList<SongSearch>? = null
    var uri: Uri? = null
    var position = -1
    var actionPlaying: ActionMusic? = null
    private var isFistTime = true
//    private var artistImageBitmap : Bitmap? = null
    private var songSearchArtistImageLink = "https://photo-resize-zmp3.zadn.vn/w94_r1x1_jpeg/"

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    inner class MyBinder : Binder() {
        val service: MusicOnlineService
            get() = this@MusicOnlineService
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        position = intent.getIntExtra("servicePosition", -1)
        val actionName = intent.getStringExtra("ActionName")
        if (position != -1 && isFistTime) {
            playMedia(position)
            isFistTime = false
        }
//        createArtistImageBitmap()
        if (actionName != null) {
            when (actionName) {
                "playPause" -> actionPlaying!!.playPauseBtnClicked()
                "next" -> actionPlaying!!.nextBtnClicked()
                "previous" -> actionPlaying!!.prevBtnClicked()
            }
        }
        return START_STICKY
    }

    fun playMedia(position: Int) {
        favoriteSongList = SongSearchFragment.songs
        searchSongList = SongSearchFragment.songSearchs
        if (mediaPlayer != null) {
//            stop()
            release()
        }
        createMediaPlayer(position)
        start()
    }

    fun start() {
        mediaPlayer!!.start()
        startAnimation()
    }

    val isPlaying: Boolean
        get() = mediaPlayer!!.isPlaying

    fun stop() {
        mediaPlayer!!.stop()
    }

    fun release() {
        mediaPlayer!!.release()
    }

    val duration: Int
        get() = mediaPlayer!!.duration

    fun seekTo(position: Int) {
        mediaPlayer!!.seekTo(position)
    }

    fun createMediaPlayer(positionInter: Int) {
        position = positionInter
        uri = if (favoriteSongList!!.size != 0) {
            Uri.parse("http://api.mp3.zing.vn/api/streaming/audio/${favoriteSongList!![position].id}/320")
        } else {
            Uri.parse("http://api.mp3.zing.vn/api/streaming/audio/${searchSongList!![position].id}/320")
        }
        mediaPlayer = MediaPlayer.create(baseContext, uri)
    }

//    private fun createArtistImageBitmap(){
//        if (favoriteSongList!!.size != 0) {
//            getBitmapFromUrlAsync(favoriteSongList!![position].thumbNail)
//        } else {
//            getBitmapFromUrlAsync(songSearchArtistImageLink+searchSongList!![position].thumb)        }
//    }

    val currentPosition: Int
        get() = mediaPlayer!!.currentPosition

    fun pause() {
        mediaPlayer!!.pause()
        stopAnimation()
    }

    fun onCompleted() {
        mediaPlayer!!.setOnCompletionListener(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (actionPlaying != null) {
            actionPlaying!!.nextBtnClicked()
        }
        createMediaPlayer(position)
        start()
        onCompleted()
    }

    fun setCallBack(actionPlaying: ActionMusic) {
        this.actionPlaying = actionPlaying
    }

    fun showNotification(playPauseBtn: Int, artistImageBitmap:Bitmap?,artistName: String?, songName: String) {
//        val intent = Intent(this, PlayerActivity::class.java)
//        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val prevIntent = Intent(this, MusicOnlineReceiver::class.java)
            .setAction(MyApp.ACTION_PREVIOUS)
        val prevPending =
            PendingIntent.getBroadcast(this, 1, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pauseIntent = Intent(this, MusicOnlineReceiver::class.java)
            .setAction(MyApp.ACTION_PLAY)
        val pausePending =
            PendingIntent.getBroadcast(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, MusicOnlineReceiver::class.java)
            .setAction(MyApp.ACTION_NEXT)
        val nextPending =
            PendingIntent.getBroadcast(this, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

//        val bitmap = getBitmapFromURL(artistImageLink)
//        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.music)

        val mediaSessionCompat = MediaSessionCompat(this, "My Audio")
        val notification = NotificationCompat.Builder(this, MyApp.CHANNEL_ID_2)
            .setSmallIcon(R.drawable.ic_baseline_music_note_24)
            .setLargeIcon(artistImageBitmap)
            .setContentTitle(songName)
            .setContentText(artistName)
//            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPending)
            .addAction(playPauseBtn, "Pause", pausePending)
            .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPending)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(0, notification)
        startForeground(1,notification)

    }

    @SuppressLint("StaticFieldLeak")
    fun showNotificationAsync(playPauseBtn: Int, artistImageUrl:String?,artistName: String?, songName: String){
        val asyncTask = object : AsyncTask<String, Void, Bitmap>(){
            override fun doInBackground(vararg params: String?): Bitmap {
                return getBitmapFromURL(params[0])!!
            }

            override fun onPostExecute(result: Bitmap?) {
                if(result != null){
                    showNotification(playPauseBtn, result,artistName,songName)
                }
            }
        }
        asyncTask.execute(artistImageUrl)
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            Log.e("tag",e.message!!)
            null
        }
    }


//    @SuppressLint("CheckResult")
//    private fun getBitmapFromURL(src: String?): Bitmap? {
//        Observable.create<String>{
//            try {
//                val url = URL(src)
//                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//                connection.doInput = true
//                connection.connect()
//                val input: InputStream = connection.inputStream
//                BitmapFactory.decodeStream(input)
//            } catch (e: IOException) {
//
//            }
//        }
//    }

    private fun startAnimation() {
        val runnable = object : Runnable {
            override fun run() {
                PlayerActivity.imageArtist!!.animate()
                    .rotationBy(360F)
                    .setDuration(30000)
                    .setInterpolator(LinearInterpolator())
                    .withEndAction(this)
                    .start()
            }
        }
        PlayerActivity.imageArtist!!.animate()
            .rotation(360F)
            .setDuration(30000)
            .setInterpolator(LinearInterpolator())
            .withEndAction(runnable)
            .start()
    }

    private fun stopAnimation() {
        PlayerActivity.imageArtist!!.animate().cancel()
    }
}