package com.unica.bxhbaihatapi.main.songonline

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.unica.bxhbaihatapi.R
import com.unica.bxhbaihatapi.databinding.ActivityPlayerBinding
import com.unica.bxhbaihatapi.db.entity.SongSearch
import com.unica.bxhbaihatapi.main.songoffline.SongData
import com.unica.bxhbaihatapi.model.song.Song
import com.unica.bxhbaihatapi.ui.base.BaseActivity
import kotlin.random.Random

class PlayerActivity : BaseActivity(), MediaPlayer.OnCompletionListener, Runnable,
    View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    companion object {
        var songOffline: SongData? = null
        var song: Song? = null
        var songSearch: SongSearch? = null
        var uri: Uri? = null
        var mediaPlayer: MediaPlayer? = null
    }

    private var position = -1
    private var shuffleBoolean = false
    private var repeatBoolean = false

    private var playThread: Thread? = null
    private var prevThread: Thread? = null
    private var nextThread: Thread? = null

    private lateinit var binding: ActivityPlayerBinding
    private var handler: Handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)
        if(song !=null){
            binding.song = song
        }
        else
            binding.songSearch = songSearch
        position = SongSearchFragment.position1
        binding.seekBar.setOnSeekBarChangeListener(this)
        runOnUiThread(Runnable {
            updateDurationAndSeekBar()
        })
        binding.shuffle.setOnClickListener(this)
        binding.repeat.setOnClickListener(this)
    }


    private fun startAnimation() {
        val runnable = object : Runnable {
            override fun run() {
                binding.songImage.animate()
                    .rotationBy(360F)
                    .setDuration(30000)
                    .setInterpolator(LinearInterpolator())
                    .withEndAction(this)
                    .start()
            }
        }
        binding.songImage.animate()
            .rotation(360F)
            .setDuration(30000)
            .setInterpolator(LinearInterpolator())
            .withEndAction(runnable)
            .start()
    }

    private fun stopAnimation() {
        binding.songImage.animate().cancel()
    }

    override fun onResume() {
        playSong()
        mediaPlayer!!.setOnCompletionListener(this)
        playThreadBtn()
        prevThreadBtn()
        nextThreadBtn()
        if (mediaPlayer!!.isPlaying) {
            startAnimation()
        } else
            stopAnimation()
        super.onResume()
    }

    private fun handlerNextOrPreviousButton(x: Int) {
        if (song != null) {
            if (mediaPlayer!!.isPlaying) {
                previousOrNextSetup(
                    R.drawable.ic_baseline_pause_24,
                    x,
                    SongSearchFragment.songs.size
                )
                mediaPlayer!!.start()
                startAnimation()
            } else {
                previousOrNextSetup(
                    R.drawable.ic_baseline_play_arrow_24,
                    x,
                    SongSearchFragment.songs.size
                )
                stopAnimation()
            }
            binding.durationTotal.text =
                formattedTime(SongSearchFragment.songs[position].duration)
        } else {
            if (mediaPlayer!!.isPlaying) {
                previousOrNextSetup(
                    R.drawable.ic_baseline_pause_24,
                    x,
                    SongSearchFragment.songSearchs.size
                )
                mediaPlayer!!.start()
                startAnimation()
            } else {
                previousOrNextSetup(
                    R.drawable.ic_baseline_play_arrow_24,
                    x,
                    SongSearchFragment.songSearchs.size
                )
                stopAnimation()
            }
            binding.durationTotal.text =
                formattedTime(SongSearchFragment.songSearchs[position].duration.toInt())
        }
        binding.seekBar.max = mediaPlayer!!.duration / 1000
    }


    private fun nextThreadBtn() {
        nextThread = object : Thread() {
            override fun run() {
                super.run()
                binding.next.setOnClickListener {
                    handlerNextOrPreviousButton(1)
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
                    handlerNextOrPreviousButton(-1)
                }
            }
        }
        (prevThread as Thread).start()
    }


    private fun previousOrNextSetup(imageInt: Int, x: Int, y: Any) {
        if (shuffleBoolean && !repeatBoolean) {
            position = Random.nextInt(y as Int) + x
            if (position < 0) {
                position = y - 1
            }
            if (position == y - 1) {
                position = 0
            }
        } else if (!shuffleBoolean && !repeatBoolean) {
            position = (position + x) % (y as Int)
            if (position < 0) {
                position = y - 1
            }
        }
        if (song != null) {
            uri =
                Uri.parse("http://api.mp3.zing.vn/api/streaming/audio/${(SongSearchFragment.songs[position]).id}/320")
            prepareSong(imageInt)
            binding.song = SongSearchFragment.songs[position]
        } else {
            uri =
                Uri.parse("http://api.mp3.zing.vn/api/streaming/audio/${(SongSearchFragment.songSearchs[position]).id}/320")
            prepareSong(imageInt)
            binding.songSearch = SongSearchFragment.songSearchs[position]
        }
    }

    private fun prepareSong(imageInt: Int) {
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        mediaPlayer = MediaPlayer.create(
            applicationContext,
            uri
        )
        binding.playPause.setBackgroundResource(imageInt)
        mediaPlayer!!.setOnCompletionListener(this@PlayerActivity)
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
            stopAnimation()
        } else {
            binding.playPause.setImageResource(R.drawable.ic_baseline_pause_24)
            mediaPlayer!!.start()
            startAnimation()
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
        if (song != null) {
            uri = Uri.parse("http://api.mp3.zing.vn/api/streaming/audio/${song!!.id}/320")
            createSongOrSongSearch(song!!.duration)
            startAnimation()
        } else {
            uri = Uri.parse("http://api.mp3.zing.vn/api/streaming/audio/${songSearch!!.id}/320")
            createSongOrSongSearch(songSearch!!.duration.toInt())
            startAnimation()
        }
    }

    private fun createSongOrSongSearch(duration: Int) {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = MediaPlayer.create(
                applicationContext,
                uri
            )
            mediaPlayer!!.start()
        } else {
            mediaPlayer = MediaPlayer.create(
                applicationContext,
                uri
            )
            mediaPlayer!!.start()
        }
        binding.seekBar.max = mediaPlayer!!.duration / 1000
        binding.durationTotal.text = formattedTime(duration)
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
        updateDurationAndSeekBar()
    }

    private fun updateDurationAndSeekBar() {
        if (mediaPlayer != null) {
            val mCurrentPosition = mediaPlayer!!.currentPosition / 1000
            binding.seekBar.progress = mCurrentPosition
            binding.durationPlayed.text = formattedTime(mCurrentPosition)
        }
        handler.postDelayed(this, 1000)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        handlerNextOrPreviousButton(1)
        binding.seekBar.max = mediaPlayer!!.duration / 1000
        if (mediaPlayer != null) {
            mediaPlayer = MediaPlayer.create(
                applicationContext,
                uri
            )
            mediaPlayer!!.start()
            startAnimation()
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