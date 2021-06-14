package com.unica.bxhbaihatapi.main.songoffline

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.unica.bxhbaihatapi.R
import com.unica.bxhbaihatapi.common.ActionMusic
import com.unica.bxhbaihatapi.common.service.MusicOfflineService
import com.unica.bxhbaihatapi.databinding.ActivitySongOfflinePlayerBinding
import kotlin.random.Random

class SongMusicPlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, Runnable,
    View.OnClickListener, ActionMusic, ServiceConnection {
    private lateinit var binding: ActivitySongOfflinePlayerBinding
    private var handler: Handler = Handler(Looper.getMainLooper())

    private var playThread: Thread? = null
    private var prevThread: Thread? = null
    private var nextThread: Thread? = null
    private var listOfSong = mutableListOf<SongData>()
    private var shuffleBoolean = false
    private var repeatBoolean = false

    companion object {
        var position = -1
        var musicOfflineService: MusicOfflineService? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_song_offline_player)
        position = SongOfflineFragment.position1
        listOfSong = SongOfflineFragment.listOfSongOffline
        playSong()
        binding.seekBar.setOnSeekBarChangeListener(this)
        this.runOnUiThread(Runnable {
            if (musicOfflineService != null) {
                val mCurrentPosition = musicOfflineService!!.currentPosition / 1000
                binding.seekBar.progress = mCurrentPosition
                binding.durationPlayed.text = formattedTime(mCurrentPosition)
            }
            handler.postDelayed(this, 1000)
        })

        binding.shuffle.setOnClickListener(this)
        binding.repeat.setOnClickListener(this)
        val intent = Intent(this, MusicOfflineService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }


    override fun onResume() {
        playThreadBtn()
        prevThreadBtn()
        nextThreadBtn()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }
    override fun nextBtnClicked() {
        if (musicOfflineService!!.isPlaying) {
            musicOfflineService!!.stop()
            musicOfflineService!!.release()
            if (shuffleBoolean && !repeatBoolean) {
                position = Random.nextInt(listOfSong.size) + 1
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = (position + 1) % listOfSong.size
            }
            musicOfflineService!!.createMediaPlayer(position)
            binding.data = listOfSong[position]
            binding.playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            musicOfflineService!!.start()
            musicOfflineService!!.onCompleted()
            musicOfflineService!!.showNotification(R.drawable.ic_baseline_pause_24)
        } else {
            musicOfflineService!!.stop()
            musicOfflineService!!.release()
            if (shuffleBoolean && !repeatBoolean) {
                position = Random.nextInt(listOfSong.size) + 1
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = (position + 1) % listOfSong.size
            }
            musicOfflineService!!.createMediaPlayer(position)
            binding.data = listOfSong[position]
            binding.playPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
            musicOfflineService!!.onCompleted()
            musicOfflineService!!.showNotification(R.drawable.ic_baseline_play_arrow_24)
        }
        binding.durationTotal.text = formattedTime(listOfSong[position].duration!!.toInt()/1000)
        binding.seekBar.max = musicOfflineService!!.duration/1000
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
                    prevBtnClicked()
                }
            }
        }
        (prevThread as Thread).start()
    }

    override fun prevBtnClicked() {
        if (musicOfflineService!!.isPlaying) {
            musicOfflineService!!.stop()
            musicOfflineService!!.release()
            if (shuffleBoolean && !repeatBoolean) {
                position = Random.nextInt(listOfSong.size) - 1
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = (position - 1) % listOfSong.size
            }
            if(position == -1){
                position = listOfSong.size -1
            }
            musicOfflineService!!.createMediaPlayer(position)
            binding.data = listOfSong[position]
            binding.playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            musicOfflineService!!.onCompleted()
            musicOfflineService!!.showNotification(R.drawable.ic_baseline_pause_24)
            musicOfflineService!!.start()
        } else {
            musicOfflineService!!.stop()
            musicOfflineService!!.release()
            if (shuffleBoolean && !repeatBoolean) {
                position = Random.nextInt(listOfSong.size) - 1
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = (position - 1) % listOfSong.size
            }
            musicOfflineService!!.createMediaPlayer(position)
            binding.data = listOfSong[position]
            binding.playPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
            musicOfflineService!!.onCompleted()
            musicOfflineService!!.showNotification(R.drawable.ic_baseline_play_arrow_24)
        }
        binding.durationTotal.text = formattedTime(listOfSong[position].duration!!.toInt()/1000)
        binding.seekBar.max = musicOfflineService!!.duration/1000
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
        if (musicOfflineService!!.isPlaying) {
            binding.playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            musicOfflineService!!.showNotification(R.drawable.ic_baseline_play_arrow_24)
            musicOfflineService!!.pause()
        } else {
            binding.playPause.setImageResource(R.drawable.ic_baseline_pause_24)
            musicOfflineService!!.showNotification(R.drawable.ic_baseline_pause_24)
            musicOfflineService!!.start()
        }
    }

    private fun playSong() {
        binding.playPause.setImageResource(R.drawable.ic_baseline_pause_24)
        val intent = Intent(this, MusicOfflineService::class.java)
        intent.putExtra("servicePosition", position)
        startService(intent)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (musicOfflineService != null && fromUser) {
            musicOfflineService!!.seekTo(progress * 1000)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun run() {
        if (musicOfflineService != null) {
            val mCurrentPosition = musicOfflineService!!.currentPosition / 1000
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

    override fun onServiceDisconnected(name: ComponentName?) {
        musicOfflineService = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder = service as MusicOfflineService.MyBinder
        musicOfflineService = myBinder.service
        musicOfflineService!!.setCallBack(this)
        binding.data = listOfSong[position]
        musicOfflineService!!.onCompleted()
        binding.durationTotal.text = formattedTime(listOfSong[position].duration!!.toInt()/1000)
        musicOfflineService!!.playMedia(position)
        binding.seekBar.max = musicOfflineService!!.duration/1000
        musicOfflineService!!.showNotification(R.drawable.ic_baseline_pause_24)
    }

    private fun formattedTime(mCurrentPosition: Int?): String {
        val totalOut: String
        val totalNew: String
        val second = (mCurrentPosition!! % 60).toString()
        val minutes = (mCurrentPosition / 60).toString()
        totalOut = "$minutes:$second"
        totalNew = "$minutes:0$second"
        return if (second.length == 1) {
            totalNew
        } else
            totalOut
    }
}