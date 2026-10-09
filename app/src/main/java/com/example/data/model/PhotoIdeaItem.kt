package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_ideas")
data class PhotoIdeaItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val boardName: String = "Inspirations",
    val tags: String = "",
    val description: String = "",
    val cameraSettings: String = "",
    val recommendedGear: String = "",
    val colorPaletteHexes: String = "#F4A261,#E76F51,#2A9D8F,#264653,#E9C46A",
    val moodTheme: String = "Golden Sunset",
    val isPinned: Boolean = false,
    val linkedContractId: Long? = null,
    val linkedContractTitle: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
