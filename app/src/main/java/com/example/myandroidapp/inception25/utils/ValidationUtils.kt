package com.example.myandroidapp.inception25.utils

import android.content.Context
import com.example.myandroidapp.R

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return email.matches(emailRegex.toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun validateEmail(email: String, context: Context): String? {
        return when {
            email.isBlank() -> context.getString(R.string.validation_required)
            !isValidEmail(email) -> context.getString(R.string.validation_email)
            else -> null
        }
    }

    fun validatePassword(password: String, context: Context): String? {
        return when {
            password.isBlank() -> context.getString(R.string.validation_required)
            password.length < 6 -> context.getString(R.string.validation_password_length)
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String, context: Context): String? {
        return if (password != confirmPassword) {
            context.getString(R.string.validation_password_match)
        } else {
            null
        }
    }
}