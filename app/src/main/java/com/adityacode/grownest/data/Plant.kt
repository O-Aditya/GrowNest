package com.adityacode.grownest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val species: String,
    val lastWatered: Long, // Timestamp in milliseconds
    val wateringFrequency: Int, // Days
    val notes: String,
    val imageUri: String? // Local file path or URI
)