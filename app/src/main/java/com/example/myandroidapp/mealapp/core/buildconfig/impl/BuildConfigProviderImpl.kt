package com.example.myandroidapp.mealapp.core.buildconfig.impl

import com.example.myandroidapp.BuildConfig
import com.example.myandroidapp.mealapp.core.buildconfig.api.BuildConfigProvider

class BuildConfigProviderImpl : BuildConfigProvider {
    override fun getMealDbApiBaseUrl(): String = BuildConfig.MEAL_DB_API_BASE_URL + BuildConfig.apiKey + "/"
    override fun getMealDbApiKey(): String = BuildConfig.apiKey
}