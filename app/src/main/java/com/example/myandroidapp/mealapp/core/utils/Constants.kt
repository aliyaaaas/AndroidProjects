package com.example.myandroidapp.mealapp.core.utils

import com.example.myandroidapp.BuildConfig

object Constants {
    const val CACHE_TTL_SECONDS = 60L

    const val KEY_MEAL_ID = "meal_id"
    const val KEY_LAST_QUERY = "last_query"

    const val PUSH_KEY_KIND = "kind"
    const val PUSH_KEY_TITLE = "title"
    const val PUSH_KEY_MESSAGE = "message"

    const val PUSH_KIND_PROMO = "promo"
    const val PUSH_KIND_AUTH = "auth"
    const val PUSH_KIND_GENERAL = "general"

    val ENABLE_HARDCODED_ERROR: Boolean get() = BuildConfig.DEBUG
}