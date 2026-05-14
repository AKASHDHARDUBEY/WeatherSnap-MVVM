package com.example.weathersnap.data.remote

import com.example.weathersnap.data.remote.dto.CityResponse
import com.example.weathersnap.data.remote.dto.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/search")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("format") format: String = "json"
    ): Response<CityResponse>

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "true",
        @Query("current_params") params: String = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m,surface_pressure"
    ): Response<WeatherResponse>
}
