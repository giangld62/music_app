package com.unica.bxhbaihatapi.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.unica.bxhbaihatapi.R
import com.unica.bxhbaihatapi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.adapter =
            ViewPagerAdapter(supportFragmentManager)
    }

}