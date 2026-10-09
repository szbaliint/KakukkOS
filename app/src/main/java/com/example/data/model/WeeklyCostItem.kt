package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_costs")
data class WeeklyCostItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String, // Groceries, Dining, Transport, Studio & Work, Utilities, Health, Entertainment, Other
    val date: String, // YYYY-MM-DD
    val dayOfWeek: String, // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    val weekNumber: Int, // 1..53
    val year: Int = 2026,
    val notes: String = "",
    val isRecurring: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
