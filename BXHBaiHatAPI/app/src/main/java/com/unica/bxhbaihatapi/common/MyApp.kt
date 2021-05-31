package com.unica.bxhbaihatapi.common

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import com.unica.bxhbaihatapi.db.AppDatabase

class MyApp : Application() {
    companion object {
        private lateinit var db: AppDatabase
        fun getDB() = db
        const val CHANNEL_ID_1 = "channel1"
        const val CHANNEL_ID_2 = "channel1"
        const val ACTION_PREVIOUS = "actionPrevious"
        const val ACTION_NEXT = "actionNext"
        const val ACTION_PLAY = "actionPlay"
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "db_version_1.sqlite"
        ).allowMainThreadQueries()
            .build()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 =
                NotificationChannel(
                    CHANNEL_ID_1, "Music Offline", NotificationManager.IMPORTANCE_HIGH
                )
            val channel2 = NotificationChannel(
                CHANNEL_ID_2,
                "Music Online", NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel1)
            notificationManager.createNotificationChannel(channel2)
        }
    }

}