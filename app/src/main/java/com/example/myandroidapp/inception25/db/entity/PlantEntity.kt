package com.example.myandroidapp.inception25.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plants",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "name"])
    ]
)
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "location")
    val location: String?,

    @ColumnInfo(name = "photo_uri")
    val photoUri: String?,

    @ColumnInfo(name = "watering_interval")
    val wateringInterval: Int = 7,

    @ColumnInfo(name = "last_watered")
    val lastWatered: Long?,

    @ColumnInfo(name = "difficulty")
    val difficulty: Int = 3,

    @ColumnInfo(name = "notes")
    val notes: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "rating", defaultValue = "3")
    val rating: Int = 3
)