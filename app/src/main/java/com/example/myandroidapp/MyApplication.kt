package com.example.myandroidapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import java.util.UUID

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        initCrashlyticsUserId()
        FirebaseCrashlytics.getInstance().log("Application started")
    }

    private fun initCrashlyticsUserId() {
        val prefs = getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE)
        val userIdKey = getString(R.string.pref_key_user_id)

        val existingUserId = prefs.getString(userIdKey, null)
        val userId = existingUserId ?: UUID.randomUUID().toString()

        if (existingUserId == null) {
            prefs.edit().putString(userIdKey, userId).apply()
        }
        FirebaseCrashlytics.getInstance().setUserId(userId)
    }
}