package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_gear")
data class PhotoGearItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String = "",
    val model: String = "",
    val category: GearCategory = GearCategory.CAMERA_BODY,
    val serialNumber: String = "",
    val purchaseDate: String = "",
    val purchasePrice: Double = 0.0,
    val condition: GearCondition = GearCondition.EXCELLENT,
    val location: String = "Main Gear Bag",
    val isInsured: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class GearCategory(val displayName: String, val iconTag: String) {
    CAMERA_BODY("Camera Body", "camera"),
    LENS("Lens", "camera_lens"),
    LIGHTING("Lighting & Flash", "flash"),
    AUDIO("Audio & Mic", "mic"),
    DRONE_GIMBAL("Drone & Gimbal", "drone"),
    ACCESSORY("Accessories", "accessories"),
    STORAGE("Storage & Cards", "sd_card")
}

enum class GearCondition(val label: String) {
    MINT("Mint 10/10"),
    EXCELLENT("Excellent 9/10"),
    GOOD("Good 8/10"),
    NEEDS_SERVICE("Needs Service"),
    RETIRED("Retired / Backup")
}
