package com.unica.bxhbaihatapi.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.unica.bxhbaihatapi.R
import com.unica.bxhbaihatapi.databinding.ActivityMainBinding
import com.unica.bxhbaihatapi.db.entity.SongSearch
import com.unica.bxhbaihatapi.main.songonline.PlayerActivity
import com.unica.bxhbaihatapi.main.songonline.ViewPagerAdapter
import com.unica.bxhbaihatapi.model.song.Song
import com.unica.bxhbaihatapi.ui.base.BaseFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
//        openFirstFragment(SongSearchFragment::class.java)
    }

//    private fun openFirstFragment(clazz: Class<out BaseFragment>) {
//        supportFragmentManager
//            .beginTransaction()
//            .add(
//                R.id.frame_container_main,
//                clazz.newInstance(),
//                clazz.name
//            )
//            .commit()
//    }
//
//    fun openHideFragment(clazz: Class<out BaseFragment>,song:Song?,songSearch:SongSearch?) {
//
//        PlayerActivity.song = song
//        PlayerActivity.songSearch = songSearch
//        supportFragmentManager.beginTransaction()
//            .hide(findFragmentVisible()!!)
//            .add(R.id.frame_container_main, clazz.newInstance(), clazz.name)
//            .addToBackStack(null)
//            .commit()
//    }
//
//
//    private fun findFragmentVisible(): Fragment? {
//        for (fragment in supportFragmentManager.fragments) {
//            if (fragment != null && fragment.isVisible)
//                return fragment
//        }
//        return null
//    }
}