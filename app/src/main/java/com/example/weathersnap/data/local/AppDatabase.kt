package com.example.weathersnap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weathersnap.data.local.entities.ReportEntity
import com.example.weathersnap.data.local.entities.WeatherCacheEntity

@Database(entities = [ReportEntity::class, WeatherCacheEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reportDao(): WeatherReportDao
}
