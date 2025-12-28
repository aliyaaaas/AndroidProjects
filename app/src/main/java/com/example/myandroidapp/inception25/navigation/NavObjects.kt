package com.example.myandroidapp.inception25.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginScreen

@Serializable
object RegisterScreen

@Serializable
object PlantsListScreen

@Serializable
object AddPlantScreen

@Serializable
object ProfileScreen

@Serializable
data class RestoreAccountScreen(
    val userId: Long,
    val email: String
)

object PlantSortOptions {
    const val NAME_ASC = "name_asc"
    const val NAME_DESC = "name_desc"
    const val NEXT_WATERING = "next_watering"
    const val DIFFICULTY_ASC = "difficulty_asc"
    const val DIFFICULTY_DESC = "difficulty_desc"
    const val TYPE = "type"
    const val DATE_ADDED = "date_added"
}