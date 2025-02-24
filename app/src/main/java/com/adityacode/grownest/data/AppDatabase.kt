package com.adityacode.grownest.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Plant::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

}