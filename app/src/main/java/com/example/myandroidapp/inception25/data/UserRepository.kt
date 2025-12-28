package com.example.myandroidapp.inception25.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.example.myandroidapp.inception25.di.ServiceLocator
import com.example.myandroidapp.inception25.mapper.UserModelMapper
import com.example.myandroidapp.inception25.model.UserDataModel

class UserRepository(
    private val mapper: UserModelMapper,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val userDao = lazy { ServiceLocator.getDatabase().userDao }

    suspend fun createNewUser(userData: UserDataModel): Long {
        return withContext(ioDispatcher) {
            val entity = mapper.map(input = userData)
            userDao.value.insertUser(entity)
        }
    }

    suspend fun loginUser(email: String, password: String): Long? {
        return withContext(ioDispatcher) {
            val user = userDao.value.getUserByEmail(email)
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
            val user = userDao.value.getUserById(userId)
            user?.let { mapper.map(it) }
        }
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return withContext(ioDispatcher) {
            val count = userDao.value.checkEmailExists(email)
            count > 0
        }
    }

    suspend fun markUserAsDeleted(userId: Long) {
        withContext(ioDispatcher) {
            userDao.value.markUserAsDeleted(userId, System.currentTimeMillis())
        }
    }

    suspend fun restoreUser(userId: Long) {
        withContext(ioDispatcher) {
            userDao.value.restoreUser(userId)
        }
    }

    suspend fun checkIfUserDeleted(email: String): Pair<Boolean, Long?> {
        return withContext(ioDispatcher) {
            val user = userDao.value.getDeletedUserByEmail(email)
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
            val cutoffDate = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
            userDao.value.deleteExpiredAccounts(cutoffDate)
        }
    }

    private fun isWithinRecoveryPeriod(deletionDate: Long?): Boolean {
        if (deletionDate == null) return false
        val sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000
        return System.currentTimeMillis() - deletionDate < sevenDaysInMillis
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}