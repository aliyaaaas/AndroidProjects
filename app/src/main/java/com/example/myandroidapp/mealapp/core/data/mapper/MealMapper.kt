package com.example.myandroidapp.mealapp.core.data.mapper

import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.mealapp.core.network.pojo.MealDto
import kotlin.collections.mapNotNull

class MealMapper {

    fun mapToDomainList(input: List<MealDto>?): List<MealModel> {
        return input?.mapNotNull { mealDto ->
            mapToDomain(mealDto)
        } ?: emptyList()
    }

    fun mapToDomain(input: MealDto?): MealModel? {
        return input?.let {
            MealModel(
                id = it.id.orEmpty(),
                name = it.name.orEmpty(),
                category = it.category.orEmpty(),
                area = it.area.orEmpty(),
                instructions = it.instructions.orEmpty(),
                imageUrl = it.imageUrl.orEmpty(),
                tags = it.tags,
                youtubeUrl = it.youtubeUrl
            )
        }
    }

    private fun String?.orEmpty(): String = this ?: ""
}