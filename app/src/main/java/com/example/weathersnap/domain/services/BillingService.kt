package com.example.weathersnap.domain.services

interface BillingService {
    fun launchPurchaseFlow(productId: String)
    fun checkSubscriptionStatus(): Boolean
}
