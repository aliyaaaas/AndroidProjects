package com.example.myandroidapp.inception25.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.example.myandroidapp.inception25.di.ServiceLocator
import com.example.myandroidapp.inception25.mapper.PlantModelMapper
import com.example.myandroidapp.inception25.model.PlantInputModel
import com.example.myandroidapp.inception25.model.PlantModel

class PlantRepository(
    private val mapper: PlantModelMapper,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val plantDao = lazy { ServiceLocator.getDatabase().plantDao }

    suspend fun addPlant(plantInput: PlantInputModel, userId: Long): Long {
        return withContext(ioDispatcher) {
            val entity = mapper.map(plantInput, userId)
            plantDao.value.insertPlant(entity)
        }
    }

    suspend fun getPlantsSortedByName(userId: Long): List<PlantModel> {
        return withContext(ioDispatcher) {
            plantDao.value.getPlantsByUserIdSortedByName(userId)
                .map { mapper.map(it) }
        }
    }

    suspend fun getPlantsSortedByNameDesc(userId: Long): List<PlantModel> {
        return withContext(ioDispatcher) {
            plantDao.value.getPlantsByUserIdSortedByNameDesc(userId)
                .map { mapper.map(it) }
        }
    }

    suspend fun getPlantsSortedByNextWatering(userId: Long): List<PlantModel> {
        return withContext(ioDispatcher) {
            plantDao.value.getPlantsByUserIdSortedByNextWatering(userId)
                .map { mapper.map(it) }
        }
    }

    suspend fun getPlantsSortedByDifficultyAsc(userId: Long): List<PlantModel> {
        return withContext(ioDispatcher) {
            plantDao.value.getPlantsByUserIdSortedByDifficultyAsc(userId)
                .map { mapper.map(it) }
        }
    }

    suspend fun getPlantsSortedByDifficultyDesc(userId: Long): List<PlantModel> {
        return withContext(ioDispatcher) {
            plantDao.value.getPlantsByUserIdSortedByDifficultyDesc(userId)
                .map { mapper.map(it) }
        }
    }

    suspend fun getPlantsSortedByType(userId: Long): List<PlantModel> {
        return withContext(ioDispatcher) {
            plantDao.value.getPlantsByUserIdSortedByType(userId)
                .map { mapper.map(it) }
        }
    }

    suspend fun getPlantsSortedByDateAdded(userId: Long): List<PlantModel> {
        return withContext(ioDispatcher) {
            plantDao.value.getPlantsByUserIdSortedByDateAdded(userId)
                .map { mapper.map(it) }
        }
    }

    suspend fun getPlantCount(userId: Long): Int {
        return withContext(ioDispatcher) {
            plantDao.value.getPlantCountByUser(userId)
        }
    }

    suspend fun getPlantsNeedingWaterCount(userId: Long): Int {
        return withContext(ioDispatcher) {
            plantDao.value.getPlantsNeedingWaterCount(userId, System.currentTimeMillis())
        }
    }

    suspend fun markPlantAsWatered(plantId: Long) {
        withContext(ioDispatcher) {
            val plant = plantDao.value.getPlantById(plantId)
            plant?.let {
                val updatedPlant = it.copy(lastWatered = System.currentTimeMillis())
                plantDao.value.updatePlant(updatedPlant)
            }
        }
    }
}