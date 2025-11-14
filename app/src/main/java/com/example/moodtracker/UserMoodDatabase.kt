package com.example.moodtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserMood::class], version = 1)
abstract class UserMoodDatabase : RoomDatabase() {
    abstract fun userMoodDao(): UserMoodDao

    companion object {
        @Volatile
        private var INSTANCE: UserMoodDatabase? = null

        fun getDatabase(context: Context): UserMoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserMoodDatabase::class.java,
                    "user_moods_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
