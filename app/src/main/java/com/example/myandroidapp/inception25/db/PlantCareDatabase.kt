package com.example.myandroidapp.inception25.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myandroidapp.inception25.db.dao.PlantDao
import com.example.myandroidapp.inception25.db.dao.UserDao
import com.example.myandroidapp.inception25.db.entity.PlantEntity
import com.example.myandroidapp.inception25.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PlantEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class PlantCareDatabase : RoomDatabase() {

    abstract val userDao: UserDao
    abstract val plantDao: PlantDao

    companion object {
        const val DATABASE_VERSION = 1
    }
}