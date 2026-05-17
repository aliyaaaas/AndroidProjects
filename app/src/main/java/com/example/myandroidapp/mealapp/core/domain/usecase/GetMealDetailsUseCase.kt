package com.example.myandroidapp.mealapp.core.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository

class GetMealDetailsUseCase(
    private val mealRepository: MealRepository
) {

    suspend operator fun invoke(mealId: String): MealModel? {
        return withContext(Dispatchers.IO) {
            mealRepository.getMealDetails(mealId)
        }
    }
}