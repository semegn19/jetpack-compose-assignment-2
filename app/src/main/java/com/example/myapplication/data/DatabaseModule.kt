package com.example.myapplication.data

import android.content.Context
import androidx.room.Room

object DatabaseModule {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val inst = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "todo_db"
            ).build()
            INSTANCE = inst
            inst
        }
    }
}