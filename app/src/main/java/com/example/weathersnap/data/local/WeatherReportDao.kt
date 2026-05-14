package com.example.weathersnap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weathersnap.data.local.entities.ReportEntity
import com.example.weathersnap.data.local.entities.WeatherCacheEntity

@Dao
interface WeatherReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("SELECT * FROM weather_reports")
    suspend fun getAllReports(): List<ReportEntity>

    @Query("SELECT * FROM weather_reports WHERE cityName = :cityName AND isDraft = 1 LIMIT 1")
    suspend fun getDraftForCity(cityName: String): ReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeatherCache(cache: WeatherCacheEntity)

    @Query("SELECT * FROM weather_cache WHERE cityName = :cityName LIMIT 1")
    suspend fun getCachedWeather(cityName: String): WeatherCacheEntity?
}
