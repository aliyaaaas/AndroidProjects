package com.example.myandroidapp.inception25.data

import com.example.myandroidapp.inception25.db.dao.PlantDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.example.myandroidapp.inception25.mapper.PlantModelMapper
import com.example.myandroidapp.inception25.model.PlantInputModel
import com.example.myandroidapp.inception25.model.PlantModel
import com.example.myandroidapp.inception25.model.PlantSortOption

class PlantRepository(
    private val plantDao: PlantDao,
    private val mapper: PlantModelMapper,
    private val ioDispatcher: CoroutineDispatcher
) {
    private var cachedPlants: List<PlantModel> = emptyList()
    private var cachedUserId: Long? = null

    suspend fun getPlants(
        userId: Long,
        sortOption: PlantSortOption? = null
    ): List<PlantModel> {
        return withContext(ioDispatcher) {
            loadPlantsIfNeeded(userId)

            return@withContext if (sortOption != null) {
                sortPlantsList(sortOption)
            } else {
                cachedPlants
            }
        }
    }

    private suspend fun loadPlantsIfNeeded(userId: Long) {
        if (cachedPlants.isEmpty() || cachedUserId != userId) {
            val entities = plantDao.getPlantsByUserId(userId)
            cachedPlants = entities.map { mapper.map(it) }
            cachedUserId = userId
        }
    }

    private fun sortPlantsList(sortOption: PlantSortOption): List<PlantModel> {
        return when (sortOption) {
            PlantSortOption.NAME_ASC ->
                cachedPlants.sortedBy { it.name.lowercase() }

            PlantSortOption.NAME_DESC ->
                cachedPlants.sortedByDescending { it.name.lowercase() }

            PlantSortOption.NEXT_WATERING ->
                cachedPlants.sortedBy { plant ->
                    if (plant.needsWatering) 0 else 1
                }.sortedBy { plant ->
                    plant.daysUntilWatering ?: Int.MAX_VALUE
                }

            PlantSortOption.DIFFICULTY_ASC ->
                cachedPlants.sortedBy { it.difficulty }

            PlantSortOption.DIFFICULTY_DESC ->
                cachedPlants.sortedByDescending { it.difficulty }

            PlantSortOption.TYPE ->
                cachedPlants.sortedBy { it.type.lowercase() }

            PlantSortOption.DATE_ADDED ->
                cachedPlants.sortedByDescending { it.createdAt }
        }
    }

    suspend fun getPlantsSortedByName(userId: Long): List<PlantModel> {
        return getPlants(userId, PlantSortOption.NAME_ASC)
    }

    suspend fun getPlantsSortedByNameDesc(userId: Long): List<PlantModel> {
        return getPlants(userId, PlantSortOption.NAME_DESC)
    }

    suspend fun getPlantsSortedByNextWatering(userId: Long): List<PlantModel> {
        return getPlants(userId, PlantSortOption.NEXT_WATERING)
    }

    suspend fun getPlantsSortedByDifficultyAsc(userId: Long): List<PlantModel> {
        return getPlants(userId, PlantSortOption.DIFFICULTY_ASC)
    }

    suspend fun getPlantsSortedByDifficultyDesc(userId: Long): List<PlantModel> {
        return getPlants(userId, PlantSortOption.DIFFICULTY_DESC)
    }

    suspend fun getPlantsSortedByType(userId: Long): List<PlantModel> {
        return getPlants(userId, PlantSortOption.TYPE)
    }

    suspend fun getPlantsSortedByDateAdded(userId: Long): List<PlantModel> {
        return getPlants(userId, PlantSortOption.DATE_ADDED)
    }

    suspend fun addPlant(plantInput: PlantInputModel, userId: Long): Long {
        return withContext(ioDispatcher) {
            val entity = mapper.map(plantInput, userId)
            val plantId = plantDao.insertPlant(entity)

            invalidateCache()

            plantId
        }
    }

    suspend fun markPlantAsWatered(plantId: Long) {
        withContext(ioDispatcher) {
            val plant = plantDao.getPlantById(plantId)
            plant?.let {
                val updatedPlant = it.copy(lastWatered = System.currentTimeMillis())
                plantDao.updatePlant(updatedPlant)
                invalidateCache()
            }
        }
    }

    private fun invalidateCache() {
        cachedPlants = emptyList()
        cachedUserId = null
    }

    suspend fun getPlantCount(userId: Long): Int {
        return withContext(ioDispatcher) {
            plantDao.getPlantCountByUser(userId)
        }
    }

    suspend fun getPlantsNeedingWaterCount(userId: Long): Int {
        return withContext(ioDispatcher) {
            val currentTime = System.currentTimeMillis()
            plantDao.getPlantsNeedingWaterCount(userId, currentTime)
        }
    }
}