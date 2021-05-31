package com.unica.bxhbaihatapi.common.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.unica.bxhbaihatapi.R
import com.unica.bxhbaihatapi.broadcastreceiver.MusicOfflineReceiver
import com.unica.bxhbaihatapi.common.MyApp
import com.unica.bxhbaihatapi.common.myinterface.ActionOffline
import com.unica.bxhbaihatapi.main.songoffline.SongData
import com.unica.bxhbaihatapi.main.songoffline.SongOfflineFragment

class MusicOfflineService : Service(), MediaPlayer.OnCompletionListener {
    var mBinder: IBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    var musicFiles = mutableListOf<SongData>()
    var uri: Uri? = null
    var position = -1
    var actionPlaying: ActionOffline? = null
    private var isFistTime = true

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    inner class MyBinder : Binder() {
        val service: MusicOfflineService
            get() = this@MusicOfflineService
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        position = intent.getIntExtra("servicePosition", -1)
        val actionName = intent.getStringExtra("ActionName")
        if (position != 1 && isFistTime) {
            playMedia(position)
            isFistTime = false
        }
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
        musicFiles = SongOfflineFragment.listOfSongOffline
        if (mediaPlayer != null) {
            stop()
            release()
        }
        createMediaPlayer(position)
        start()
    }

    fun start() {
        mediaPlayer!!.start()
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
        uri = Uri.parse(musicFiles[position].path)
        mediaPlayer = MediaPlayer.create(baseContext, uri)
    }

    val currentPosition: Int
        get() = mediaPlayer!!.currentPosition

    fun pause() {
        mediaPlayer!!.pause()
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

    fun setCallBack(actionPlaying: ActionOffline) {
        this.actionPlaying = actionPlaying
    }

    fun showNotification(playPauseBtn: Int) {
//        val intent = Intent(this, PlayerActivity::class.java)
//        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val prevIntent = Intent(this, MusicOfflineReceiver::class.java)
            .setAction(MyApp.ACTION_PREVIOUS)
        val prevPending =
            PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pauseIntent = Intent(this, MusicOfflineReceiver::class.java)
            .setAction(MyApp.ACTION_PLAY)
        val pausePending =
            PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, MusicOfflineReceiver::class.java)
            .setAction(MyApp.ACTION_NEXT)
        val nextPending =
            PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.music)
        val mediaSessionCompat = MediaSessionCompat(this, "My Audio")
        val notification = NotificationCompat.Builder(this, MyApp.CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_music_note_24)
            .setLargeIcon(bitmap)
            .setContentTitle(musicFiles[position].title)
            .setContentText(musicFiles[position].artist)
//            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPending)
            .addAction(playPauseBtn, "Pause", pausePending)
            .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPending)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1)
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
//        startForeground(1,notification)

    }
}