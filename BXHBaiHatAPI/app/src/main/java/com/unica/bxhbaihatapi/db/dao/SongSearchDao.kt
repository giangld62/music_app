package com.unica.bxhbaihatapi.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unica.bxhbaihatapi.db.entity.SongSearch

@Dao
interface SongSearchDao {
    @Query("select * from SongSearch")
    fun getAll():MutableList<SongSearch>

    @Query("select * from SongSearch where name = :name")
    fun getByName(name:String):MutableList<SongSearch>

    @Query("delete from SongSearch where name =:name")
    fun deleteByName(name:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(music:MutableList<SongSearch>)
}