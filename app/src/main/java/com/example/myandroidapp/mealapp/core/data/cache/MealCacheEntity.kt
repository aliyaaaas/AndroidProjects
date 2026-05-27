package com.example.myandroidapp.mealapp.core.data.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_cache")
data class MealCacheEntity(
    @PrimaryKey
    val query: String,
    val mealsJson: String,
    val timestamp: Long
)