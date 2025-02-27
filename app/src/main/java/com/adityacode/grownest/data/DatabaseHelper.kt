package com.adityacode.grownest.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseHelper {
    private var instance: AppDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create new table with desired schema
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS plants_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    species TEXT NOT NULL,
                    lastWatered INTEGER NOT NULL,
                    wateringFrequency INTEGER NOT NULL,
                    notes TEXT,
                    imageUri TEXT
                )
            """)

            // Copy data from old table to new table
            database.execSQL("""
                INSERT INTO plants_new (id, name, species, lastWatered, wateringFrequency, notes, imageUri)
                SELECT id, name, species, lastWatered, wateringFrequency, notes, imageUri FROM plants
            """)

            // Remove old table
            database.execSQL("DROP TABLE plants")

            // Rename new table to match expected name
            database.execSQL("ALTER TABLE plants_new RENAME TO plants")
        }
    }

    @Synchronized
    fun getDatabase(context: Context): AppDatabase {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "plant_database"
            )
            .addMigrations(MIGRATION_1_2) // Add migration
            .build()
        }
        return instance!!
    }
}