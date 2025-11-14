package com.example.moodtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_mood_notes")
data class UserMoodNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val timestamp: Long,
    val location: String,
    val weather: String,
    val temperature: Double,
    val note: String,
    val isSynced: Boolean = false
)
