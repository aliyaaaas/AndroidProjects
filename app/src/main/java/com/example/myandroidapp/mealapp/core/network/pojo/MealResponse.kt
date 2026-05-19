package com.example.myandroidapp.mealapp.core.network.pojo

import com.google.gson.annotations.SerializedName

data class MealResponse(
    @SerializedName("meals")
    val meals: List<MealDto>?
)
