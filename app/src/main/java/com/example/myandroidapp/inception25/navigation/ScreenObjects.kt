package com.example.myandroidapp.inception25.navigation

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class NotesScreenObject(
    val email: String,
    val notes: List<com.example.myandroidapp.inception25.model.NoteDataModel> = emptyList()
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AddNoteScreenObject(val email: String)