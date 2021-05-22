package com.unica.bxhbaihatapi.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.unica.bxhbaihatapi.main.songoffline.SongOfflineFragment
import com.unica.bxhbaihatapi.main.songonline.SongSearchFragment

class ViewPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return if(position == 1)
            SongOfflineFragment()
        else
            SongSearchFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if(position == 0)
            "Song Online"
        else
            "Song Offline"
    }
}