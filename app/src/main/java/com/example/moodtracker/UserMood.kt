package com.example.moodtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_moods")
data class UserMood(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val location: String,
    val weather: String,
    val mainNote: String,
    val selectedMood: String,
    val time: String, // only "HH:mm"

)
