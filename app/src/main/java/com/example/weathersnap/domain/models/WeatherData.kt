package com.example.weathersnap.domain.models

data class WeatherData(
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double
)
