package com.example.moodtracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserMoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mood: UserMood)

    @Query("SELECT * FROM user_moods WHERE userId = :userId ORDER BY id DESC")
    suspend fun getAllMoods(userId: String): List<UserMood>

    @Query("SELECT COUNT(*) FROM user_moods WHERE userId = :userId")
    suspend fun countMoodsForUser(userId: String): Int
}