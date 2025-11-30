package com.example.myandroidapp.ui
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class CoroutineSettings(
    val count: Int = 10,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val isSequential: Boolean = true,
    val isLazy: Boolean = false,
    val runsInBackground: Boolean = true
)