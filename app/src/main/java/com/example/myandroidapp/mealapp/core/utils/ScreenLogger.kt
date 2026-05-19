package com.example.myandroidapp.mealapp.core.utils

import android.content.Context
import androidx.annotation.StringRes
import com.google.firebase.crashlytics.FirebaseCrashlytics

object ScreenLogger {
    fun logScreenOpen(context: Context, @StringRes screenNameResId: Int) {
        val screenName = context.getString(screenNameResId)
        FirebaseCrashlytics.getInstance().log("Screen opened: $screenName")
    }
}