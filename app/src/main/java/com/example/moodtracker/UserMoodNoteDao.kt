package com.example.moodtracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserMoodNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: UserMoodNote)

    @Query("SELECT * FROM user_mood_notes WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestNote(userId: String): UserMoodNote?

    @Query("SELECT * FROM user_mood_notes WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllNotes(userId: String): List<UserMoodNote>
}
