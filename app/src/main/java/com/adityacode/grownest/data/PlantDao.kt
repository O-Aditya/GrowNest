package com.adityacode.grownest.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlantDao {
    @Insert
    suspend fun insert(plant: Plant)

    @Update
    suspend fun update(plant: Plant)


    @Query("SELECT * FROM plants")
    fun getAllPlants(): LiveData<List<Plant>>
}