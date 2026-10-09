package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpectedCostStatus {
    PLANNING,      // Budgeting & estimating
    COMMITTED,     // Booked, enrolled, or confirmed commitment
    FUNDED,        // 100% saved or covered
    PAID           // Completed & settled
}

enum class ExpectedCostUrgency {
    REQUIRED,      // Essential commitment with firm date (e.g. tuition, visa, renewal)
    PLANNED,       // Important targeted goal (e.g. vacation travel, certification)
    FLEXIBLE       // Adaptable timeline
}

@Entity(tableName = "expected_costs")
data class ExpectedCostItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val estimatedAmount: Double,
    val allocatedAmount: Double = 0.0,
    val category: String,
    val targetDate: String = "",
    val status: ExpectedCostStatus = ExpectedCostStatus.PLANNING,
    val urgency: ExpectedCostUrgency = ExpectedCostUrgency.PLANNED,
    val notes: String = "",
    val isMandatory: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val remainingAmount: Double
        get() = (estimatedAmount - allocatedAmount).coerceAtLeast(0.0)

    val progressPercent: Float
        get() = if (estimatedAmount > 0) ((allocatedAmount / estimatedAmount) * 100).coerceIn(0.0, 100.0).toFloat() else 0f

    val isFullyFunded: Boolean
        get() = allocatedAmount >= estimatedAmount && estimatedAmount > 0
}
