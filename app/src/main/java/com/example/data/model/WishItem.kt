package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishes")
data class WishItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetPrice: Double,
    val currentSaved: Double = 0.0,
    val priority: WishPriority = WishPriority.MEDIUM,
    val category: String = "General",
    val targetDate: String = "",
    val notes: String = "",
    val isFulfilled: Boolean = false,
    val isGearWish: Boolean = false,
    val gearCategory: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val progressPercent: Float
        get() = if (targetPrice > 0) ((currentSaved / targetPrice) * 100).coerceIn(0.0, 100.0).toFloat() else 0f

    val remainingAmount: Double
        get() = (targetPrice - currentSaved).coerceAtLeast(0.0)
}

enum class WishPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
