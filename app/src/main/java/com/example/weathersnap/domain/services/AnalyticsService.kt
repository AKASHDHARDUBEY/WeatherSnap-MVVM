package com.example.weathersnap.domain.services

interface AnalyticsService {
    fun logEvent(eventName: String, params: Map<String, Any>)
    fun logUserError(error: String)
}
