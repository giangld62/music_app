package com.unica.bxhbaihatapi.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unica.bxhbaihatapi.db.dao.SongSearchDao
import com.unica.bxhbaihatapi.db.entity.SongSearch

@Database(entities = [SongSearch::class],version = 2)
abstract class AppDatabase :RoomDatabase(){
    abstract fun songSearchDao():SongSearchDao
}