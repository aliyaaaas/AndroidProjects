package com.example.myandroidapp.mealapp.core.data.cache

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.myandroidapp.mealapp.core.domain.model.MealModel

class MealCacheRepository(
    private val context: Context,
    private val gson: Gson
) {
    private val database = MealCacheDatabase.getInstance(context)
    private val dao = database.mealCacheDao()

    companion object {
        const val CACHE_TTL_SECONDS = 60L
    }

    suspend fun getCachedMeals(query: String): List<MealModel>? {
        val cached = dao.getByQuery(query)
        val now = System.currentTimeMillis()
        val cacheAgeSeconds = (now - (cached?.timestamp ?: 0)) / 1000

        if (cached != null && cacheAgeSeconds < CACHE_TTL_SECONDS) {
            val type = object : TypeToken<List<MealModel>>() {}.type
            return gson.fromJson(cached.mealsJson, type)
        }
        return null
    }

    suspend fun saveToCache(query: String, meals: List<MealModel>) {
        val mealsJson = gson.toJson(meals)
        val entity = MealCacheEntity(
            query = query,
            mealsJson = mealsJson,
            timestamp = System.currentTimeMillis()
        )
        dao.insert(entity)
    }

    suspend fun clearExpiredCache() {
        val olderThan = System.currentTimeMillis() - (CACHE_TTL_SECONDS * 1000)
        dao.deleteOlderThan(olderThan)
    }
}
