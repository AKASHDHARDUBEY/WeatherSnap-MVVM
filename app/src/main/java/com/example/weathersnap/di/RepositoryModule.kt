package com.example.weathersnap.di

import com.example.weathersnap.data.repository.WeatherRepositoryImpl
import com.example.weathersnap.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    companion object {
        @dagger.Provides
        @Singleton
        fun provideAnalyticsService(): com.example.weathersnap.domain.services.AnalyticsService = object : com.example.weathersnap.domain.services.AnalyticsService {
            override fun logEvent(eventName: String, params: Map<String, Any>) {
                android.util.Log.d("Analytics", "Event Logged: $eventName")
            }
            override fun logUserError(error: String) {
                android.util.Log.e("Analytics", "Error Logged: $error")
            }
        }

        @dagger.Provides
        @Singleton
        fun provideBillingService(): com.example.weathersnap.domain.services.BillingService = object : com.example.weathersnap.domain.services.BillingService {
            override fun launchPurchaseFlow(productId: String) {
                android.util.Log.d("Billing", "Purchase flow launched for: $productId")
            }
            override fun checkSubscriptionStatus(): Boolean {
                return false
            }
        }
    }
}
