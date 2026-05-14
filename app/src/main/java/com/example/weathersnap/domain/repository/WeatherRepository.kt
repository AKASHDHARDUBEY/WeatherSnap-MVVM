package com.example.weathersnap.domain.repository

import com.example.weathersnap.data.local.entities.ReportEntity
import com.example.weathersnap.domain.models.City
import com.example.weathersnap.domain.models.WeatherData
import com.example.weathersnap.util.Resource

interface WeatherRepository {
    suspend fun searchCities(query: String): Resource<List<City>>
    suspend fun getWeather(lat: Double, lon: Double, cityName: String): Resource<WeatherData>
    suspend fun saveReport(report: ReportEntity)
    suspend fun getAllReports(): Any
}
