package com.example.myandroidapp.inception25.model

data class PlantInputModel(
    val name: String = "",
    val type: String = "",
    val location: String? = null,
    val photoUri: String? = null,
    val wateringInterval: Int = 7,
    val difficulty: Int = 3,
    val notes: String? = null,
    val rating: Int = 3
)