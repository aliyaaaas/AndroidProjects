package com.example.myandroidapp.mealapp.core.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealCacheDao {

    @Query("SELECT * FROM meal_cache WHERE `query` = :query")
    suspend fun getByQuery(query: String): MealCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MealCacheEntity)

    @Query("DELETE FROM meal_cache WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)

    @Query("DELETE FROM meal_cache WHERE timestamp < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)
}