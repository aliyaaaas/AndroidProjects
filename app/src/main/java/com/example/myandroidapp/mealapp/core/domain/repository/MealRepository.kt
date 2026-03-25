package com.example.myandroidapp.mealapp.core.domain.repository

import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import kotlinx.coroutines.flow.SharedFlow

interface MealRepository {

    suspend fun searchByQuery(query: String): List<MealModel>

    suspend fun getMealDetails(mealId: String): MealModel?

    val dataSourceEvent: SharedFlow<String>
}