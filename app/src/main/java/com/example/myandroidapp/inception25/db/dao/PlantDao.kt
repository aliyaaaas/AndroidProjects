package com.example.myandroidapp.inception25.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myandroidapp.inception25.db.entity.PlantEntity

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Query("SELECT * FROM plants WHERE user_id = :userId")
    suspend fun getPlantsByUserId(userId: Long): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE user_id = :userId ORDER BY name ASC")
    suspend fun getPlantsByUserIdSortedByName(userId: Long): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE user_id = :userId ORDER BY name DESC")
    suspend fun getPlantsByUserIdSortedByNameDesc(userId: Long): List<PlantEntity>

    @Query("""
        SELECT * FROM plants 
        WHERE user_id = :userId 
        ORDER BY 
            CASE 
                WHEN last_watered IS NULL THEN 1 
                ELSE last_watered + (watering_interval * 24 * 60 * 60 * 1000) 
            END ASC
    """)
    suspend fun getPlantsByUserIdSortedByNextWatering(userId: Long): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE user_id = :userId ORDER BY difficulty ASC")
    suspend fun getPlantsByUserIdSortedByDifficultyAsc(userId: Long): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE user_id = :userId ORDER BY difficulty DESC")
    suspend fun getPlantsByUserIdSortedByDifficultyDesc(userId: Long): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE user_id = :userId ORDER BY type ASC")
    suspend fun getPlantsByUserIdSortedByType(userId: Long): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getPlantsByUserIdSortedByDateAdded(userId: Long): List<PlantEntity>

    @Query("SELECT COUNT(*) FROM plants WHERE user_id = :userId")
    suspend fun getPlantCountByUser(userId: Long): Int

    @Query("""
        SELECT COUNT(*) FROM plants 
        WHERE user_id = :userId 
        AND (last_watered IS NULL 
             OR last_watered + (watering_interval * 24 * 60 * 60 * 1000) < :currentTime)
    """)
    suspend fun getPlantsNeedingWaterCount(userId: Long, currentTime: Long): Int

    @Update
    suspend fun updatePlant(plant: PlantEntity)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)

    @Query("DELETE FROM plants WHERE user_id = :userId")
    suspend fun deleteAllPlantsByUser(userId: Long)

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: Long): PlantEntity?
}