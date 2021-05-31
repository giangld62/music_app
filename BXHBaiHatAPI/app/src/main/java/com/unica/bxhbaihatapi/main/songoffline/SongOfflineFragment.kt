package com.unica.bxhbaihatapi.main.songoffline

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.unica.bxhbaihatapi.databinding.FragmentSongOfflineBinding
import com.unica.bxhbaihatapi.main.MainActivity
import com.unica.bxhbaihatapi.main.songonline.PlayerActivity
import java.util.*


class SongOfflineFragment : Fragment(), SongOfflineAdapter.ISongOffline, TextWatcher {

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
        listOfSongOffline = getAllSong()
        binding.rcvSong.adapter = SongOfflineAdapter(this)
        binding.rcvSong.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.edtSearch.addTextChangedListener(this)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        getAllSong()
        binding.rcvSong.adapter = SongOfflineAdapter(this)
        super.onResume()
    }

    private fun getAllSong() : MutableList<SongData>{
        val listOfSong = mutableListOf<SongData>()
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
            val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val pathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            while (cursor.moveToNext()){
                val album = cursor.getString(albumIndex)
                val title = cursor.getString(titleIndex)
                val duration = cursor.getString(durationIndex)
                val path = cursor.getString(pathIndex)
                val artist = cursor.getString(artistIndex)
                listOfSong.add(SongData(path,title,artist,album,duration))
            }
            cursor.close()
        }
        return listOfSong
    }

    override fun getCount(): Int {
        return listOfSongOffline.size
    }

    override fun getData(position: Int): SongData {
        return listOfSongOffline[position]
    }

    override fun onItemClick(position: Int) {
        if(PlayerActivity.mediaPlayer!=null){
            PlayerActivity.mediaPlayer!!.release()
            PlayerActivity.mediaPlayer = null
        }
        position1 = position
        startActivity(Intent(context, SongOfflinePlayerActivity::class.java))
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val userInput = binding.edtSearch.text.toString().toLowerCase(Locale.ROOT).trim()
        val tempList = mutableListOf<SongData>()
        listOfSongOffline = if(userInput.isEmpty()){
            getAllSong()
        }else{
            for(song in listOfSongOffline){
                if(song.title?.toLowerCase(Locale.ROOT)?.contains(userInput)!!){
                    tempList.add(song)
                }
            }
            tempList
        }
        binding.rcvSong.adapter!!.notifyDataSetChanged()
    }

    override fun afterTextChanged(s: Editable?) {
    }

}