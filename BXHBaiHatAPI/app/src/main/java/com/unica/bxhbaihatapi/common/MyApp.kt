package com.unica.bxhbaihatapi.common

import android.app.Application
import androidx.room.Room
import com.unica.bxhbaihatapi.db.AppDatabase

class MyApp : Application() {
    companion object {
        private lateinit var db: AppDatabase
        fun getDB() = db
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "db_version_1.sqlite"
        ).allowMainThreadQueries()
            .build()
    }
}