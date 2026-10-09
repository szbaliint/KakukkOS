package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType {
    DIVIDEND,
    MAK_COUPON,
    MARKET_FLUCTUATION,
    BUDGET_ALERT,
    SECURITY_EVENT
}

@Entity(tableName = "notifications")
data class MarketNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: NotificationType,
    val isRead: Boolean = false,
    val symbolOrTag: String = "",
    val changePercent: Double? = null,
    val amountCredit: Double? = null
)
