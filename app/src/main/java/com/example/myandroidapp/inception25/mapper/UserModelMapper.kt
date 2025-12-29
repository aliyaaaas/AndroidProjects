package com.example.myandroidapp.inception25.mapper

import com.example.myandroidapp.inception25.db.entity.UserEntity
import com.example.myandroidapp.inception25.model.UserDataModel
import java.security.MessageDigest

class UserModelMapper {

    fun map(input: UserDataModel): UserEntity {
        return UserEntity(
            email = input.email,
            passwordHash = hashPassword(input.password),
            name = input.name
        )
    }

    fun map(input: UserEntity): UserDataModel {
        return UserDataModel(
            email = input.email,
            password = "",
            name = input.name
        )
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}