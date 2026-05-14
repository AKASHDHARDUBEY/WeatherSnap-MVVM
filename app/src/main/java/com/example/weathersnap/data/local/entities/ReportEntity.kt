package com.example.weathersnap.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val temp: Double,
    val condition: String,
    val notes: String,
    val imagePath: String,
    val originalSize: Long,
    val compressedSize: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val isDraft: Boolean = false
)
