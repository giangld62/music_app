package com.unica.bxhbaihatapi.main.songoffline

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.unica.bxhbaihatapi.R
import com.unica.bxhbaihatapi.databinding.ActivitySongOfflinePlayerBinding
import kotlin.random.Random

class SongOfflinePlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, Runnable, MediaPlayer.OnCompletionListener, View.OnClickListener {
    private lateinit var binding: ActivitySongOfflinePlayerBinding
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var playThread: Thread? = null
    private var prevThread: Thread? = null
    private var nextThread: Thread? = null
    private var listOfSong = mutableListOf<SongData>()
    private var position = -1
    private var shuffleBoolean = false
    private var repeatBoolean = false

    companion object {
        var song: SongData? = null
        var uri: Uri? = null
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_song_offline_player)
        position = SongOfflineFragment.position1
        song = SongOfflineFragment.listOfSongOffline[position]
        listOfSong = SongOfflineFragment.listOfSongOffline
        binding.data = song
        playSong()
        mediaPlayer!!.setOnCompletionListener(this)
        binding.seekBar.setOnSeekBarChangeListener(this)
        this.runOnUiThread(Runnable {
            if (mediaPlayer != null) {
                val mCurrentPosition = mediaPlayer!!.currentPosition / 1000
                binding.seekBar.progress = mCurrentPosition
                binding.durationPlayed.text = formattedTime(mCurrentPosition)
            }
            handler.postDelayed(this, 1000)
        })

        binding.shuffle.setOnClickListener(this)
        binding.repeat.setOnClickListener(this)

    }




    override fun onResume() {
        playSong()
        playThreadBtn()
        prevThreadBtn()
        nextThreadBtn()
        super.onResume()
    }

    private fun nextBtnClicked(){
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            if(shuffleBoolean && !repeatBoolean){
                position = Random.nextInt(listOfSong.size)+1
            }
            else if(!shuffleBoolean && !repeatBoolean){
                position = (position +1) % listOfSong.size
            }
            uri = Uri.parse(listOfSong[position].path)
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            binding.data = listOfSong[position]
            binding.playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            mediaPlayer!!.start()
            mediaPlayer!!.setOnCompletionListener(this)
        }
        else{
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            if(shuffleBoolean && !repeatBoolean){
                position = Random.nextInt(listOfSong.size)+1
            }
            else if(!shuffleBoolean && !repeatBoolean){
                position = (position +1) % listOfSong.size
            }
            uri = Uri.parse(listOfSong[position].path)
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            binding.data = listOfSong[position]
            binding.playPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
            mediaPlayer!!.setOnCompletionListener(this)
        }
    }

    private fun nextThreadBtn() {
        nextThread = object : Thread() {
            override fun run() {
                super.run()
                binding.next.setOnClickListener {
                    nextBtnClicked()
                }
            }
        }
        (nextThread as Thread).start()
    }

    private fun prevThreadBtn() {
        prevThread = object : Thread() {
            override fun run() {
                super.run()
                binding.previous.setOnClickListener {
                    if (mediaPlayer!!.isPlaying) {
                        mediaPlayer!!.stop()
                        mediaPlayer!!.release()
                        if(shuffleBoolean && !repeatBoolean){
                            position = Random.nextInt(listOfSong.size)-1
                        }
                        else if(!shuffleBoolean && !repeatBoolean){
                            position = (position -1) % listOfSong.size
                        }
                        uri = Uri.parse(listOfSong[position].path)
                        mediaPlayer = MediaPlayer.create(applicationContext,uri)
                        binding.data = listOfSong[position]
                        binding.playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                        mediaPlayer!!.setOnCompletionListener(this@SongOfflinePlayerActivity)
                        mediaPlayer!!.start()
                    }
                    else{
                        mediaPlayer!!.stop()
                        mediaPlayer!!.release()
                        if(shuffleBoolean && !repeatBoolean){
                            position = Random.nextInt(listOfSong.size)-1
                        }
                        else if(!shuffleBoolean && !repeatBoolean){
                            position = (position -1) % listOfSong.size
                        }
                        uri = Uri.parse(listOfSong[position].path)
                        mediaPlayer = MediaPlayer.create(applicationContext,uri)
                        binding.data = listOfSong[position]
                        binding.playPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                        mediaPlayer!!.setOnCompletionListener(this@SongOfflinePlayerActivity)
                    }
                }
            }
        }
        (prevThread as Thread).start()
    }

    private fun playThreadBtn() {
        playThread = object : Thread() {
            override fun run() {
                super.run()
                binding.playPause.setOnClickListener {
                    playPauseBtnClicked()
                }
            }
        }
        (playThread as Thread).start()
    }

    private fun playPauseBtnClicked() {
        if (mediaPlayer!!.isPlaying) {
            binding.playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            mediaPlayer!!.pause()
        } else {
            binding.playPause.setImageResource(R.drawable.ic_baseline_pause_24)
            mediaPlayer!!.start()
        }
    }


    private fun formattedTime(mCurrentPosition: Int): String {
        val totalOut: String
        val totalNew: String
        val second = (mCurrentPosition % 60).toString()
        val minutes = (mCurrentPosition / 60).toString()
        totalOut = "$minutes:$second"
        totalNew = "$minutes:0$second"
        return if (second.length == 1) {
            totalNew
        } else
            totalOut
    }

    private fun playSong() {
        binding.playPause.setImageResource(R.drawable.ic_baseline_pause_24)
        uri = Uri.parse(song!!.path)
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
            mediaPlayer!!.start()
        } else {
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
            mediaPlayer!!.start()
        }
        binding.seekBar.max = mediaPlayer!!.duration / 1000
        val durationTotal = (song!!.duration!!.toInt()) / 1000
        binding.durationTotal.text = formattedTime(durationTotal)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (mediaPlayer != null && fromUser) {
            mediaPlayer!!.seekTo(progress * 1000)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun run() {
        if (mediaPlayer != null) {
            val mCurrentPosition = mediaPlayer!!.currentPosition / 1000
            binding.seekBar.progress = mCurrentPosition
            binding.durationPlayed.text = formattedTime(mCurrentPosition)
        }
        handler.postDelayed(this, 1000)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextBtnClicked()
        if(mediaPlayer!=null){
            mediaPlayer = MediaPlayer.create(applicationContext,uri)
            mediaPlayer!!.start()
            mediaPlayer!!.setOnCompletionListener(this)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.shuffle -> {
                if (shuffleBoolean) {
                    shuffleBoolean = false
                    binding.shuffle.setImageResource(R.drawable.ic_baseline_shuffle_24_off)
                } else {
                    shuffleBoolean = true
                    binding.shuffle.setImageResource(R.drawable.ic_baseline_shuffle_24_on)
                }
            }
            R.id.repeat -> {
                if (repeatBoolean) {
                    repeatBoolean = false
                    binding.repeat.setImageResource(R.drawable.ic_baseline_repeat_24_off)
                } else {
                    repeatBoolean = true
                    binding.repeat.setImageResource(R.drawable.ic_baseline_repeat_24)
                }
            }
        }
    }
}