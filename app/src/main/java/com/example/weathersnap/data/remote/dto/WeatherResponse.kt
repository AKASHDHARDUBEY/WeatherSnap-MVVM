package com.example.weathersnap.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("relative_humidity_2m") val relative_humidity_2m: Int,
    @SerializedName("weather_code") val weather_code: Int,
    @SerializedName("wind_speed_10m") val wind_speed_10m: Double,
    @SerializedName("surface_pressure") val surface_pressure: Double
)
