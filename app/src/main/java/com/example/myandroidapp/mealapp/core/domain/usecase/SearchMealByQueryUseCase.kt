package com.example.myandroidapp.mealapp.core.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository

class SearchMealByQueryUseCase(
    private val mealRepository: MealRepository
) {

    suspend operator fun invoke(query: String): List<MealModel> {
        return withContext(Dispatchers.IO) {
            mealRepository.searchByQuery(query)
        }
    }
}