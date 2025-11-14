package com.example.moodtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserMoodNote::class], version = 1, exportSchema = false)
abstract class UserMoodNoteDatabase : RoomDatabase() {

    abstract fun userMoodNoteDao(): UserMoodNoteDao

    companion object {
        @Volatile
        private var INSTANCE: UserMoodNoteDatabase? = null

        fun getDatabase(context: Context): UserMoodNoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserMoodNoteDatabase::class.java,
                    "user_mood_note_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
