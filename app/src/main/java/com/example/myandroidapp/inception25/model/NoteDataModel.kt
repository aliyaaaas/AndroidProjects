package com.example.myandroidapp.inception25.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class NoteDataModel(
    val title: String,
    val content: String
)