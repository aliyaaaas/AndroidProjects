package com.example.myandroidapp.inception25.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.example.myandroidapp.inception25.db.dao.UserDao
import com.example.myandroidapp.inception25.mapper.UserModelMapper
import com.example.myandroidapp.inception25.model.UserDataModel

class UserRepository(
    private val userDao: UserDao,
    private val mapper: UserModelMapper,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun createNewUser(userData: UserDataModel): Long {
        return withContext(ioDispatcher) {
            val entity = mapper.map(input = userData)
            userDao.insertUser(entity)
        }
    }

    suspend fun loginUser(email: String, password: String): Long? {
        return withContext(ioDispatcher) {
            val user = userDao.getUserByEmail(email)
            val passwordHash = hashPassword(password)

            if (user != null && user.passwordHash == passwordHash && !user.isDeleted) {
                user.id
            } else {
                null
            }
        }
    }

    suspend fun getUserProfile(userId: Long): UserDataModel? {
        return withContext(ioDispatcher) {
            val user = userDao.getUserById(userId)
            user?.let { mapper.map(it) }
        }
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return withContext(ioDispatcher) {
            val count = userDao.checkEmailExists(email)
            count > 0
        }
    }

    suspend fun markUserAsDeleted(userId: Long) {
        withContext(ioDispatcher) {
            userDao.markUserAsDeleted(userId, System.currentTimeMillis())
        }
    }

    suspend fun restoreUser(userId: Long) {
        withContext(ioDispatcher) {
            userDao.restoreUser(userId)
        }
    }

    suspend fun checkIfUserDeleted(email: String): Pair<Boolean, Long?> {
        return withContext(ioDispatcher) {
            val user = userDao.getDeletedUserByEmail(email)
            if (user != null) {
                val isWithinRecovery = isWithinRecoveryPeriod(user.deletionDate)
                Pair(isWithinRecovery, user.id)
            } else {
                Pair(false, null)
            }
        }
    }

    suspend fun deleteExpiredAccounts() {
        withContext(ioDispatcher) {
            val cutoffDate = System.currentTimeMillis() - RECOVERY_PERIOD_MILLIS
            userDao.deleteExpiredAccounts(cutoffDate)
        }
    }

    private fun isWithinRecoveryPeriod(deletionDate: Long?): Boolean {
        if (deletionDate == null) return false
        return System.currentTimeMillis() - deletionDate < RECOVERY_PERIOD_MILLIS
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private companion object {
        const val RECOVERY_PERIOD_DAYS = 7L
        const val HOURS_IN_DAY = 24L
        const val MINUTES_IN_HOUR = 60L
        const val SECONDS_IN_MINUTE = 60L
        const val MILLISECONDS_IN_SECOND = 1000L

        const val RECOVERY_PERIOD_MILLIS = RECOVERY_PERIOD_DAYS *
                HOURS_IN_DAY *
                MINUTES_IN_HOUR *
                SECONDS_IN_MINUTE *
                MILLISECONDS_IN_SECOND
    }
}