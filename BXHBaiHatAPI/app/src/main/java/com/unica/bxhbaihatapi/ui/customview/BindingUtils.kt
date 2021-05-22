package com.unica.bxhbaihatapi.ui.customview

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.unica.bxhbaihatapi.R

object BindingUtils {
    //ta ca cac phuong thuc trong nay deu la static

    //muon su dung custom databing thi: apply plugin: 'kotlin-kapt' trong build.gradlew
    @JvmStatic
    @BindingAdapter("loadImageInt")
    fun loadImageInt(iv: ImageView, imageId: Int) {
        Glide.with(iv)
            .load(imageId)
            .into(iv)
    }

    @JvmStatic
    @BindingAdapter("loadImageLink")
    fun loadImageInt(iv: ImageView, link: String?) {
        if (link == null) {
            Glide.with(iv)
                .load(R.drawable.gaixinh)
                .into(iv)
            return
        }
        Glide.with(iv)
            .load(link)
            .into(iv)
    }

    @JvmStatic
    @BindingAdapter("setText")
    fun setText(tv: TextView, value: String?) {
        tv.setText(value)
    }

}