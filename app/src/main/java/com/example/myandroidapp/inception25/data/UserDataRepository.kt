package com.example.myandroidapp.inception25.data

import android.content.SharedPreferences

object UserDataRepository {

    private var sharedPref: SharedPreferences? = null

    fun provideSharedPrefs(sp: SharedPreferences) {
        if (sharedPref == null) sharedPref = sp
    }

    fun saveCurrentUserId(userId: Long) {
        sharedPref?.edit()?.apply {
            putLong(Keys.CURRENT_USER_ID, userId)
            putLong(Keys.LAST_LOGIN_TIME, System.currentTimeMillis())
            apply()
        }
    }

    fun getCurrentUserId(): Long? {
        return sharedPref?.getLong(Keys.CURRENT_USER_ID, -1L).takeIf { it != -1L }
    }

    fun clearSession() {
        sharedPref?.edit()?.apply {
            remove(Keys.CURRENT_USER_ID)
            remove(Keys.LAST_LOGIN_TIME)
        }?.commit()

        Thread.sleep(100)
    }

    fun savePlantSortType(sortType: String) {
        sharedPref?.edit()?.putString(Keys.PLANT_SORT_TYPE, sortType)?.apply()
    }

    fun getPlantSortType(): String {
        return sharedPref?.getString(Keys.PLANT_SORT_TYPE, Keys.SortDefaults.NAME_ASC) ?: Keys.SortDefaults.NAME_ASC
    }

}