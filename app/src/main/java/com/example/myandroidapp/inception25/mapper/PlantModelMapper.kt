package com.example.myandroidapp.inception25.mapper

import com.example.myandroidapp.inception25.db.entity.PlantEntity
import com.example.myandroidapp.inception25.model.PlantModel
import com.example.myandroidapp.inception25.model.PlantInputModel

class PlantModelMapper {

    fun map(input: PlantEntity): PlantModel {
        val needsWatering = PlantModel.calculateNeedsWatering(
            input.lastWatered,
            input.wateringInterval
        )
        val daysUntilWatering = PlantModel.calculateDaysUntilWatering(
            input.lastWatered,
            input.wateringInterval
        )

        return PlantModel(
            id = input.id,
            userId = input.userId,
            name = input.name,
            type = input.type,
            location = input.location,
            photoUri = input.photoUri,
            wateringInterval = input.wateringInterval,
            lastWatered = input.lastWatered,
            difficulty = input.difficulty,
            notes = input.notes,
            createdAt = input.createdAt,
            rating = input.rating,
            needsWatering = needsWatering,
            daysUntilWatering = daysUntilWatering
        )
    }

    fun map(input: PlantInputModel, userId: Long): PlantEntity {
        return PlantEntity(
            userId = userId,
            name = input.name,
            type = input.type,
            location = input.location,
            photoUri = input.photoUri,
            wateringInterval = input.wateringInterval,
            lastWatered = null,
            difficulty = input.difficulty,
            notes = input.notes,
            rating = input.rating
        )
    }
}