package com.unica.bxhbaihatapi.main.songoffline

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.unica.bxhbaihatapi.databinding.FragmentSongOfflineBinding
import com.unica.bxhbaihatapi.main.MainActivity
import com.unica.bxhbaihatapi.main.songonline.PlayerActivity


class SongOfflineFragment : Fragment(), SongOfflineAdapter.ISongOffline {

    private lateinit var binding : FragmentSongOfflineBinding
    companion object{
        var position1 = -1
        var listOfSongOffline = mutableListOf<SongData>()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongOfflineBinding.inflate(inflater,container,false)
        getAllSong()
        binding.rcvSong.adapter = SongOfflineAdapter(this)
        binding.rcvSong.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onResume() {
        getAllSong()
        binding.rcvSong.adapter = SongOfflineAdapter(this)
        super.onResume()
    }

    private fun getAllSong() : MutableList<SongData>{
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST
        )
        val cursor = (activity as MainActivity).contentResolver.query(uri,projection,null,null,null)
        if(cursor!=null){
            while (cursor.moveToNext()){
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val path = cursor.getString(3)
                val artist = cursor.getString(4)
                listOfSongOffline.add(SongData(path,title,artist,album,duration))
            }
            cursor.close()
        }
        return listOfSongOffline
    }

    override fun getCount(): Int {
        return listOfSongOffline.size
    }

    override fun getData(position: Int): SongData {
        return listOfSongOffline[position]
    }

    override fun onItemClick(position: Int) {
        if(PlayerActivity.mediaPlayer!=null){
            PlayerActivity.mediaPlayer!!.stop()
            PlayerActivity.mediaPlayer!!.release()
        }
        position1 = position
        startActivity(Intent(context, SongOfflinePlayerActivity::class.java))
    }

}