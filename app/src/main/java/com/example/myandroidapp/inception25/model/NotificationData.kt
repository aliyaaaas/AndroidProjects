package com.example.myandroidapp.inception25.model

import kotlin.random.Random

data class NotificationData(
    val id: Int = Random.nextInt(1000, 9999),
    val title: String,
    val content: String? = null,
    val priority: NotificationPriority = NotificationPriority.MEDIUM,
    val shouldExpand: Boolean = false,
    val shouldOpenApp: Boolean = false,
    val hasReplyAction: Boolean = false
)

enum class NotificationPriority(val importance: Int) {
    MIN(1), LOW(2), MEDIUM(3), HIGH(4)
}