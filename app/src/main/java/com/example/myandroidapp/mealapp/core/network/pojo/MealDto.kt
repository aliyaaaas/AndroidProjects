package com.example.myandroidapp.mealapp.core.network.pojo

import com.google.gson.annotations.SerializedName

data class MealDto(
    @SerializedName("idMeal")
    val id: String?,
    @SerializedName("strMeal")
    val name: String?,
    @SerializedName("strCategory")
    val category: String?,
    @SerializedName("strArea")
    val area: String?,
    @SerializedName("strInstructions")
    val instructions: String?,
    @SerializedName("strMealThumb")
    val imageUrl: String?,
    @SerializedName("strTags")
    val tags: String?,
    @SerializedName("strYoutube")
    val youtubeUrl: String?
)
