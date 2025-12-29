package com.example.myandroidapp.inception25.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myandroidapp.inception25.db.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email AND is_deleted = 0 LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId AND is_deleted = 0")
    suspend fun getUserById(userId: Long): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET is_deleted = 1, deletion_date = :deletionDate WHERE id = :userId")
    suspend fun markUserAsDeleted(userId: Long, deletionDate: Long)

    @Query("UPDATE users SET is_deleted = 0, deletion_date = NULL WHERE id = :userId")
    suspend fun restoreUser(userId: Long)

    @Query("DELETE FROM users WHERE is_deleted = 1 AND deletion_date < :cutoffDate")
    suspend fun deleteExpiredAccounts(cutoffDate: Long)

    @Query("SELECT COUNT(*) FROM users WHERE email = :email AND is_deleted = 0")
    suspend fun checkEmailExists(email: String): Int

    @Query("SELECT * FROM users WHERE email = :email AND is_deleted = 1 LIMIT 1")
    suspend fun getDeletedUserByEmail(email: String): UserEntity?
}