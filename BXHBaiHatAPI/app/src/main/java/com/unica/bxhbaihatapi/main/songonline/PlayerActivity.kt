package com.unica.bxhbaihatapi.main.songonline

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.*
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.unica.bxhbaihatapi.R
import com.unica.bxhbaihatapi.common.ActionMusic
import com.unica.bxhbaihatapi.common.service.MusicOnlineService
import com.unica.bxhbaihatapi.databinding.ActivityPlayerBinding
import com.unica.bxhbaihatapi.db.entity.SongSearch
import com.unica.bxhbaihatapi.model.song.Song
import com.unica.bxhbaihatapi.ui.base.BaseActivity
import kotlin.random.Random

class PlayerActivity : BaseActivity(), Runnable,
    ActionMusic,
    View.OnClickListener, SeekBar.OnSeekBarChangeListener,ServiceConnection {
    companion object {
        var song: Song? = null
        var songSearch: SongSearch? = null
        var musicOnlineService : MusicOnlineService? = null
        var position = -1
        var imageArtist : ImageView? = null
        var artistImageBitmap : Bitmap? = null
    }
    
    private var shuffleBoolean = false
    private var repeatBoolean = false

    private var playThread: Thread? = null
    private var prevThread: Thread? = null
    private var nextThread: Thread? = null

    private lateinit var binding: ActivityPlayerBinding
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var songSearchArtistImageLink = "https://photo-resize-zmp3.zadn.vn/w94_r1x1_jpeg/"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)
        imageArtist = binding.songImage
        if(song !=null){
            binding.song = song
        }
        else{
            binding.songSearch = songSearch
        }

        position = SongSearchFragment.position1
        playSong()
        binding.seekBar.setOnSeekBarChangeListener(this)
        runOnUiThread(Runnable {
            updateDurationAndSeekBar()
        })
        binding.shuffle.setOnClickListener(this)
        binding.repeat.setOnClickListener(this)

        val intent = Intent(this, MusicOnlineService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }

    override fun onResume() {
        playThreadBtn()
        prevThreadBtn()
        nextThreadBtn()
        super.onResume()
    }

    private fun handlerNextOrPreviousButton(x: Int) {
        if (song != null) {
            if (musicOnlineService!!.isPlaying) {
                previousOrNextSetup(
                    R.drawable.ic_baseline_pause_24,
                    x,
                    SongSearchFragment.songs.size
                )
                musicOnlineService!!.start()
                musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_pause_24,
                    SongSearchFragment.songs[position].thumbNail,
                    SongSearchFragment.songs[position].artistsNames,
                    SongSearchFragment.songs[position].title
                )
            } else {
                previousOrNextSetup(
                    R.drawable.ic_baseline_play_arrow_24,
                    x,
                    SongSearchFragment.songs.size
                )
                musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_play_arrow_24,
                    SongSearchFragment.songs[position].thumbNail,
                    SongSearchFragment.songs[position].artistsNames,
                    SongSearchFragment.songs[position].title
                )
            }
            binding.durationTotal.text =
                formattedTime(SongSearchFragment.songs[position].duration)
        } else {
            if (musicOnlineService!!.isPlaying) {
                previousOrNextSetup(
                    R.drawable.ic_baseline_pause_24,
                    x,
                    SongSearchFragment.songSearchs.size
                )
                musicOnlineService!!.start()
                musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_pause_24,
                    songSearchArtistImageLink+SongSearchFragment.songSearchs[position].thumb,
                    SongSearchFragment.songSearchs[position].artist,
                    SongSearchFragment.songSearchs[position].name
                )
            } else {
                previousOrNextSetup(
                    R.drawable.ic_baseline_play_arrow_24,
                    x,
                    SongSearchFragment.songSearchs.size
                )
                musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_play_arrow_24,
                    songSearchArtistImageLink+SongSearchFragment.songSearchs[position].thumb,
                    SongSearchFragment.songSearchs[position].artist,
                    SongSearchFragment.songSearchs[position].name
                )
            }
            binding.durationTotal.text =
                formattedTime(SongSearchFragment.songSearchs[position].duration.toInt())
        }
        binding.seekBar.max = musicOnlineService!!.duration / 1000
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
            prepareSong(imageInt)
            binding.song = SongSearchFragment.songs[position]
        } else {
            prepareSong(imageInt)
            binding.songSearch = SongSearchFragment.songSearchs[position]
        }
    }

    private fun prepareSong(imageInt: Int) {
        musicOnlineService!!.stop()
        musicOnlineService!!.release()
        musicOnlineService!!.createMediaPlayer(position)
        binding.playPause.setBackgroundResource(imageInt)
        musicOnlineService!!.onCompleted()
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

    override fun playPauseBtnClicked() {
        if(song != null){
            if (musicOnlineService!!.isPlaying) {
                binding.playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                musicOnlineService!!.pause()
                musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_play_arrow_24,
                    SongSearchFragment.songs[position].thumbNail,
                    SongSearchFragment.songs[position].artistsNames,
                    SongSearchFragment.songs[position].title
                )

            } else {
                binding.playPause.setImageResource(R.drawable.ic_baseline_pause_24)
                musicOnlineService!!.start()
                musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_pause_24,
                    SongSearchFragment.songs[position].thumbNail,
                    SongSearchFragment.songs[position].artistsNames,
                    SongSearchFragment.songs[position].title
                )
            }
        }
        else{
            if (musicOnlineService!!.isPlaying) {
                binding.playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                musicOnlineService!!.pause()
                musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_play_arrow_24,
                    songSearchArtistImageLink+SongSearchFragment.songSearchs[position].thumb,
                    SongSearchFragment.songSearchs[position].artist,
                    SongSearchFragment.songSearchs[position].name
                )
            } else {
                binding.playPause.setImageResource(R.drawable.ic_baseline_pause_24)
                musicOnlineService!!.start()
                musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_pause_24,
                    songSearchArtistImageLink+SongSearchFragment.songSearchs[position].thumb,
                    SongSearchFragment.songSearchs[position].artist,
                    SongSearchFragment.songSearchs[position].name
                )
            }
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
        val intent = Intent(this, MusicOnlineService::class.java)
        intent.putExtra("servicePosition",position)
        startService(intent)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (musicOnlineService != null && fromUser) {
            musicOnlineService!!.seekTo(progress * 1000)
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
        if (musicOnlineService != null) {
            val mCurrentPosition = musicOnlineService!!.currentPosition / 1000
            binding.seekBar.progress = mCurrentPosition
            binding.durationPlayed.text = formattedTime(mCurrentPosition)
        }
        handler.postDelayed(this, 1000)
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

    override fun nextBtnClicked() {
        handlerNextOrPreviousButton(1)
    }

    override fun prevBtnClicked() {
        handlerNextOrPreviousButton(-1)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicOnlineService = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder = service as MusicOnlineService.MyBinder
        musicOnlineService = myBinder.service
        musicOnlineService!!.setCallBack(this)
        musicOnlineService!!.playMedia(position)
        musicOnlineService!!.onCompleted()
        if(song!=null){
            binding.durationTotal.text = formattedTime(SongSearchFragment.songs[position].duration)
            musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_pause_24,
                SongSearchFragment.songs[position].thumbNail,
                SongSearchFragment.songs[position].artistsNames,
                SongSearchFragment.songs[position].title)
        }
        else{
            binding.durationTotal.text = formattedTime(SongSearchFragment.songSearchs[position].duration.toInt())
            musicOnlineService!!.showNotificationAsync(R.drawable.ic_baseline_pause_24,
                songSearchArtistImageLink+SongSearchFragment.songSearchs[position].thumb,
                SongSearchFragment.songSearchs[position].artist,
                SongSearchFragment.songSearchs[position].name)
        }
        binding.seekBar.max = musicOnlineService!!.duration/1000

    }

}