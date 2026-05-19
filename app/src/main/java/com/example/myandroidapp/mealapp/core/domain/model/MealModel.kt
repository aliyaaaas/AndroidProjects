package com.example.myandroidapp.mealapp.core.domain.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealModel(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val imageUrl: String,
    val tags: String?,
    val youtubeUrl: String?
) : Parcelable