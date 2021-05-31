package com.unica.bxhbaihatapi.main.songonline

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.unica.bxhbaihatapi.R
import com.unica.bxhbaihatapi.common.Utils
import com.unica.bxhbaihatapi.databinding.FragmentSearchSongBinding
import com.unica.bxhbaihatapi.db.entity.SongSearch
import com.unica.bxhbaihatapi.main.MainActivity
import com.unica.bxhbaihatapi.main.songoffline.SongOfflinePlayerActivity
import com.unica.bxhbaihatapi.model.song.Song
import com.unica.bxhbaihatapi.ui.base.BaseFragment


class SongSearchFragment : BaseFragment(), View.OnClickListener, SongAdapter.ISongSearch {
    private var model: SongSearchModel? = null
    private var binding: FragmentSearchSongBinding? = null
    private lateinit var fav: MenuItem

    companion object {
        val songs = mutableListOf<Song>()
        val songSearchs = mutableListOf<SongSearch>()
        var songPath = ""
        var songSearchPath = ""
        var position1 = -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchSongBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = SongSearchModel()
        binding?.btnSearch?.setOnClickListener(this)
        val mrg = GridLayoutManager(
            context,
            2
        )
        mrg.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (getData(position) is Song) {
                    return 1
                } else {
                    return 2
                }
            }
        }
        binding?.rc?.layoutManager = mrg
        binding?.rc?.adapter = SongAdapter(this)
        register()
        model?.getSongFavourite()
    }

    override fun onClick(v: View) {
        if ("".equals(binding?.edtSearch?.text?.toString())) {
            model?.getSongFavourite()
        } else {
            model?.getSong(
                binding?.edtSearch?.text?.toString()!!
            )
        }

    }

    //register
    fun register() {
        model?.songRes?.observe(viewLifecycleOwner, Observer {
            //noi nhan du lieu
            songs.clear()
            songSearchs.clear()
            songs.addAll(it)
            binding?.rc?.adapter?.notifyDataSetChanged()
        })

        model?.songSearchRes?.observe(viewLifecycleOwner, Observer {
            songSearchs.clear()
            songs.clear()
            songSearchs.addAll(it)
            binding?.rc?.adapter?.notifyDataSetChanged()
        })
    }

    override fun getCount(): Int {
        return if (songs.size > 0) songs.size else songSearchs.size
    }

    override fun getData(position: Int): Any {
        if (songs.size > 0) {
            if (songs[position].album != null) {
                songs[position].thumbNail = songs[position].album!!.thumbnailMedium
            }
        }
        return if (songs.size > 0) songs[position] else songSearchs[position]
    }

    override fun onItemClick(position: Int) {
        if (SongOfflinePlayerActivity.musicOfflineService != null) {
            SongOfflinePlayerActivity.musicOfflineService!!.release()
            SongOfflinePlayerActivity.musicOfflineService = null
        }
        if (getData(position) is SongSearch) {
            position1 = position
            songSearchPath = "http://api.mp3.zing.vn/api/streaming/audio/${
                (getData(position) as SongSearch)
                    .id
            }/320"
            songPath = ""
            PlayerActivity.songSearch = songSearchs[position]
            PlayerActivity.song = null
            PlayerActivity.songOffline = null
            startActivity(
                Intent(
                    context,
                    PlayerActivity::class.java
                )
            )
        } else {
            position1 = position
            songPath = "http://api.mp3.zing.vn/api/streaming/audio/${
                (getData(position) as Song)
                    .id
            }/320"
            songSearchPath = ""
            PlayerActivity.song = songs[position]
            PlayerActivity.songOffline = null
            PlayerActivity.songSearch = null
            startActivity(
                Intent(
                    context,
                    PlayerActivity::class.java
                )
            )
        }
    }

    override fun onItemClickDownload(position: Int) {
        val dialog = AlertDialog.Builder(context)
        dialog.apply {
            setTitle("Thông Báo")
            setMessage("Download bài hát?")
            setIcon(R.drawable.ic_baseline_download_24)
            setPositiveButton("OK") { dialog, which ->
                Utils.downloadMp3Async(
                    "http://api.mp3.zing.vn/api/streaming/audio/${
                        (getData(position) as SongSearch)
                            .id
                    }/320",
                    (getData(position) as SongSearch).name
                )
                Toast.makeText(context, "Đang download", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Hủy bỏ") { dialog, which ->
                dialog.dismiss()
            }
        }.create().show()
    }

}


