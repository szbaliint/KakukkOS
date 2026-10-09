package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "life_modules")
data class LifeOsModuleItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moduleKey: String,
    val title: String,
    val subtitle: String,
    val iconKey: String,
    val category: String,
    val isEnabled: Boolean = true,
    val isCustom: Boolean = false,
    val customNotes: String = "",
    val badgeText: String = "",
    val orderIndex: Int = 0
)
