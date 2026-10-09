package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_contracts")
data class PhotoContract(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val clientName: String,
    val clientEmail: String = "",
    val clientPhone: String = "",
    val shootDate: String = "",
    val shootStartTime: String = "10:00",
    val shootEndTime: String = "16:00",
    val shootLocation: String = "",
    val totalAmount: Double = 0.0,
    val depositPaid: Double = 0.0,
    val status: ContractStatus = ContractStatus.SIGNED,
    val deliverables: String = "",
    val deliverableDeadline: String = "",
    val notes: String = "",
    val isPaymentRecorded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val remainingBalance: Double
        get() = (totalAmount - depositPaid).coerceAtLeast(0.0)

    val paymentProgressPercent: Float
        get() = if (totalAmount > 0) ((depositPaid / totalAmount) * 100).coerceIn(0.0, 100.0).toFloat() else 100f
}

enum class ContractStatus(val label: String) {
    DRAFT("Draft"),
    SENT("Proposal Sent"),
    SIGNED("Signed & Confirmed"),
    SHOOT_COMPLETED("Shoot Completed"),
    DELIVERED("Gallery Delivered"),
    COMPLETED_PAID("Completed & Paid"),
    CANCELLED("Cancelled")
}
