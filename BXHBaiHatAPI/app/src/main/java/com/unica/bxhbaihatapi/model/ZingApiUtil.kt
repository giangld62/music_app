package com.unica.bxhbaihatapi.model

import android.util.Log
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ZingApiUtil{
    @JvmStatic
    fun createZingApi(baseLink:String="http://mp3.zing.vn") : ZingApi {
        val http = OkHttpClient.Builder()
            .callTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(
                LoggingInterceptor.Builder()
                    .setLevel(Level.BASIC)
                    .log(Log.VERBOSE)
                    .addHeader("cityCode","53")
                    .addQueryParam("moonStatus", "crescent")
                    .build()
            )
            .build()
        return Retrofit.Builder()
            .baseUrl(baseLink)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(http)
            .build()
            .create(ZingApi::class.java)
    }
}