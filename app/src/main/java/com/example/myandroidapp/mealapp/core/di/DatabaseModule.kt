package com.example.myandroidapp.mealapp.core.di

import android.content.Context
import androidx.room.Room
import com.example.myandroidapp.mealapp.core.data.cache.MealCacheDao
import com.example.myandroidapp.mealapp.core.data.cache.MealCacheDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideMealCacheDatabase(@ApplicationContext context: Context): MealCacheDatabase =
        Room.databaseBuilder(context, MealCacheDatabase::class.java, "meal_cache_database").build()

    @Provides
    fun provideMealCacheDao(database: MealCacheDatabase):
            MealCacheDao = database.mealCacheDao()
}