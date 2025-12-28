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
    const val ERROR_DATABASE_NOT_INITIALIZED = "Database is not initialized"

    private var plantCareDatabase: PlantCareDatabase? = null

    private val userModelMapper = UserModelMapper()
    private val plantModelMapper = PlantModelMapper()

    private val _userRepository = UserRepository(
        mapper = userModelMapper,
        ioDispatcher = Dispatchers.IO
    )

    private val _plantRepository = PlantRepository(
        mapper = plantModelMapper,
        ioDispatcher = Dispatchers.IO
    )

    fun initDatabase(appCtx: Context) {
        plantCareDatabase = Room.databaseBuilder(
            appCtx,
            PlantCareDatabase::class.java,
            DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    fun getDatabase(): PlantCareDatabase {
        return plantCareDatabase ?: throw IllegalStateException(ERROR_DATABASE_NOT_INITIALIZED)
    }


    fun getUserRepository(): UserRepository = _userRepository

    fun getPlantRepository(): PlantRepository = _plantRepository
}