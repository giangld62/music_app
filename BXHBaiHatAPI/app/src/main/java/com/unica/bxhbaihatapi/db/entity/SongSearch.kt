package com.unica.bxhbaihatapi.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SongSearch")
data class SongSearch (
    val artist: String,
    val artistIds: String,
    val block: String,
    val disDPlatform: String,
    val disSPlatform: String,
    @ColumnInfo(name = "disable_platform_web")
    val disablePlatformWeb: String,
    val duration: String,
    val genreIds: String,
    val hasVideo: String,
    @PrimaryKey
    val id: String,
    val name: String,
    val radioPid: String,
    val streamingStatus: String,
    val thumb: String,
    val thumbVideo: String,
    @ColumnInfo(name = "zing_choice")
    val zingChoice: String
    )