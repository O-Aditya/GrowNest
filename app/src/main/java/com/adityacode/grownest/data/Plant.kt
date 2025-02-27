package com.adityacode.grownest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val species: String,
    val wateringFrequency: Int,
    val lastWatered: Long = System.currentTimeMillis(),
    val imageUrl: String? = null
)