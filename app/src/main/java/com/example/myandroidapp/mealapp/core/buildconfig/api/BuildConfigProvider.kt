package com.example.myandroidapp.mealapp.core.buildconfig.api

interface BuildConfigProvider {
    fun getMealDbApiBaseUrl(): String
    fun getMealDbApiKey(): String
}