package com.example.myandroidapp.mealapp.core.data.cache

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [MealCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MealCacheDatabase : RoomDatabase() {

    abstract fun mealCacheDao(): MealCacheDao

    companion object {
        @Volatile
        private var INSTANCE: MealCacheDatabase? = null

        fun getInstance(context: Context): MealCacheDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealCacheDatabase::class.java,
                    "meal_cache_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
