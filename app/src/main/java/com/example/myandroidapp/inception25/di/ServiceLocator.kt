package com.example.myandroidapp.inception25.di

import android.content.Context
import androidx.room.Room
import com.example.myandroidapp.inception25.Constants
import kotlinx.coroutines.Dispatchers
import com.example.myandroidapp.inception25.data.PlantRepository
import com.example.myandroidapp.inception25.data.UserRepository
import com.example.myandroidapp.inception25.db.PlantCareDatabase
import com.example.myandroidapp.inception25.mapper.PlantModelMapper
import com.example.myandroidapp.inception25.mapper.UserModelMapper

object ServiceLocator {

    private const val DB_NAME = Constants.DATABASE_NAME
    private const val ERROR_DATABASE_NOT_INITIALIZED = "Database is not initialized"
    private const val ERROR_USER_REPOSITORY_NOT_INITIALIZED = "UserRepository not initialized"
    private const val ERROR_PLANT_REPOSITORY_NOT_INITIALIZED = "PlantRepository not initialized"

    private var plantCareDatabase: PlantCareDatabase? = null

    private val userModelMapper = UserModelMapper()
    private val plantModelMapper = PlantModelMapper()

    private var _userRepository: UserRepository? = null
    private var _plantRepository: PlantRepository? = null

    fun initDatabase(appCtx: Context) {
        plantCareDatabase = Room.databaseBuilder(
            appCtx,
            PlantCareDatabase::class.java,
            DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

        initRepositories()
    }

    private fun initRepositories() {
        _userRepository = UserRepository(
            userDao = getDatabase().userDao,
            mapper = userModelMapper,
            ioDispatcher = Dispatchers.IO
        )

        _plantRepository = PlantRepository(
            plantDao = getDatabase().plantDao,
            mapper = plantModelMapper,
            ioDispatcher = Dispatchers.IO
        )
    }


    fun getDatabase(): PlantCareDatabase {
        return plantCareDatabase ?: throw IllegalStateException(ERROR_DATABASE_NOT_INITIALIZED)
    }

    fun getUserRepository(): UserRepository {
        return _userRepository ?: throw IllegalStateException(ERROR_USER_REPOSITORY_NOT_INITIALIZED)
    }

    fun getPlantRepository(): PlantRepository {
        return _plantRepository ?: throw IllegalStateException(ERROR_PLANT_REPOSITORY_NOT_INITIALIZED)
    }
}