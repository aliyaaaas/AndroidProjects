package com.example.myandroidapp.inception25.model

data class PlantModel(
    val id: Long,
    val userId: Long,
    val name: String,
    val type: String,
    val location: String?,
    val photoUri: String?,
    val wateringInterval: Int,
    val lastWatered: Long?,
    val difficulty: Int,
    val notes: String?,
    val createdAt: Long,
    val rating: Int,
    val needsWatering: Boolean,
    val daysUntilWatering: Int?
) {
    companion object {
        private const val HOURS_IN_DAY = 24
        private const val MINUTES_IN_HOUR = 60
        private const val SECONDS_IN_MINUTE = 60
        private const val MILLISECONDS_IN_SECOND = 1000L

        private val MILLISECONDS_IN_DAY = HOURS_IN_DAY *
                MINUTES_IN_HOUR *
                SECONDS_IN_MINUTE *
                MILLISECONDS_IN_SECOND

        fun calculateNeedsWatering(lastWatered: Long?, wateringInterval: Int): Boolean {
            if (lastWatered == null) return true
            val nextWatering = lastWatered + (wateringInterval * MILLISECONDS_IN_DAY)
            return System.currentTimeMillis() > nextWatering
        }

        fun calculateDaysUntilWatering(lastWatered: Long?, wateringInterval: Int): Int? {
            if (lastWatered == null) return null
            val nextWatering = lastWatered + (wateringInterval * MILLISECONDS_IN_DAY)
            val days = (nextWatering - System.currentTimeMillis()) / MILLISECONDS_IN_DAY
            return days.toInt().coerceAtLeast(0)
        }
    }
}