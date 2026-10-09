package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_tasks")
data class PhotoTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contractId: Long? = null,
    val contractTitle: String? = null,
    val title: String,
    val dueDate: String = "",
    val dueTime: String = "10:00",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val category: String = "Shoot Prep",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskPriority(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent")
}
