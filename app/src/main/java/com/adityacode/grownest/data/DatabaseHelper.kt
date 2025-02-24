package com.adityacode.grownest.data

import android.content.Context
import androidx.room.Room

object DatabaseHelper {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (instance == null) {
            synchronized(this) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "grownest_db"
                ).build()
            }
        }

        return instance!!
    }
}