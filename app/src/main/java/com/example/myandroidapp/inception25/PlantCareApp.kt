package com.example.myandroidapp.inception25

import android.app.Application
import com.example.myandroidapp.inception25.data.UserDataRepository
import com.example.myandroidapp.inception25.di.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlantCareApp : Application() {

    override fun onCreate() {
        super.onCreate()

        ServiceLocator.initDatabase(appCtx = this)

        val sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        UserDataRepository.provideSharedPrefs(sp)

        cleanupExpiredAccounts()
    }


    private fun cleanupExpiredAccounts() {
        CoroutineScope(Dispatchers.IO).launch {
            ServiceLocator.getUserRepository().deleteExpiredAccounts()
        }
    }

    companion object {
        private const val SHARED_PREFS_NAME = "plantcare_prefs"
    }
}