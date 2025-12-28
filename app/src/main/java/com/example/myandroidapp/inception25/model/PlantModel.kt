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
        fun calculateNeedsWatering(lastWatered: Long?, wateringInterval: Int): Boolean {
            if (lastWatered == null) return true
            val nextWatering = lastWatered + (wateringInterval * 24 * 60 * 60 * 1000L)
            return System.currentTimeMillis() > nextWatering
        }

        fun calculateDaysUntilWatering(lastWatered: Long?, wateringInterval: Int): Int? {
            if (lastWatered == null) return null
            val nextWatering = lastWatered + (wateringInterval * 24 * 60 * 60 * 1000L)
            val days = (nextWatering - System.currentTimeMillis()) / (24 * 60 * 60 * 1000L)
            return days.toInt().coerceAtLeast(0)
        }
    }
}