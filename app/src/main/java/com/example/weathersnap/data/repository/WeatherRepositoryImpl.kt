package com.example.weathersnap.data.repository

import com.example.weathersnap.data.local.WeatherReportDao
import com.example.weathersnap.data.local.entities.ReportEntity
import com.example.weathersnap.data.remote.WeatherApi
import com.example.weathersnap.domain.models.City
import com.example.weathersnap.domain.models.WeatherData
import com.example.weathersnap.domain.repository.WeatherRepository
import com.example.weathersnap.util.Resource
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherReportDao
) : WeatherRepository {

    private val cityCache = mutableMapOf<String, List<City>>()

    override suspend fun searchCities(query: String): Resource<List<City>> {
        if (query.length < 3) return Resource.Error("Enter at least 3 letters")

        if (cityCache.containsKey(query)) {
            return Resource.Success(cityCache[query]!!)
        }

        return try {
            val response = api.searchCities(query)
            if (response.isSuccessful && response.body() != null) {
                val cities = response.body()!!.results?.map { 
                    City(it.name, it.latitude, it.longitude, it.country) 
                } ?: emptyList()
                
                cityCache[query] = cities
                Resource.Success(cities)
            } else {
                Resource.Error("City not found")
            }
        } catch (e: Exception) {
            Resource.Error("Network failure: ${e.localizedMessage}")
        }
    }

    override suspend fun getWeather(lat: Double, lon: Double, cityName: String): Resource<WeatherData> {
        return try {
            val response = api.getWeather(lat, lon)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.current
                val weatherData = WeatherData(cityName, data.temperature, mapWeatherCode(data.weather_code), data.relative_humidity_2m, data.wind_speed_10m, data.surface_pressure)
                
                dao.saveWeatherCache(
                    com.example.weathersnap.data.local.entities.WeatherCacheEntity(
                        cityName, data.temperature, mapWeatherCode(data.weather_code), 
                        data.relative_humidity_2m, data.wind_speed_10m, data.surface_pressure
                    )
                )
                
                Resource.Success(weatherData)
            } else {
                val cached = dao.getCachedWeather(cityName)
                if (cached != null) {
                    Resource.Success(WeatherData(cached.cityName, cached.temperature, cached.condition, cached.humidity, cached.windSpeed, cached.pressure))
                } else {
                    Resource.Error("No internet and no cached data")
                }
            }
        } catch (e: Exception) {
            val cached = dao.getCachedWeather(cityName)
            if (cached != null) {
                Resource.Success(WeatherData(cached.cityName, cached.temperature, cached.condition, cached.humidity, cached.windSpeed, cached.pressure))
            } else {
                Resource.Error("Network failure")
            }
        }
    }

    private fun mapWeatherCode(code: Int): String {
        return when(code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Partly cloudy"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rainy"
            else -> "Cloudy"
        }
    }

    override suspend fun saveReport(report: ReportEntity) = dao.insertReport(report)
    override suspend fun getAllReports(): List<ReportEntity> = dao.getAllReports().filter { !it.isDraft }
    override suspend fun getDraftForCity(cityName: String): ReportEntity? = dao.getDraftForCity(cityName)
    override suspend fun deleteReportById(id: Int) = dao.deleteReportById(id)
}
