package com.adityacode.grownest.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.adityacode.grownest.data.AppDatabase
import com.adityacode.grownest.data.Plant
import com.adityacode.grownest.data.PlantRepository
import com.adityacode.grownest.worker.ReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PlantViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlantRepository
    val plants: LiveData<List<Plant>>

    init {
        Log.d("PlantViewModel", "Initializing ViewModel")
        val plantDao = AppDatabase.getDatabase(application).plantDao()
        repository = PlantRepository(plantDao)
        plants = repository.allPlants.also {
            it.observeForever { plants ->
                Log.d("PlantViewModel", "Plants updated in ViewModel: ${plants.size}")
            }
        }
    }

    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    fun addPlant(plant: Plant) {
        viewModelScope.launch {
            try {
                repository.insertPlant(plant)
                _operationStatus.value = OperationStatus.Success("Plant added successfully")
                Log.d("PlantViewModel", "Plant added: ${plant.name}")
            } catch (e: Exception) {
                Log.e("PlantViewModel", "Error adding plant", e)
                _operationStatus.value = OperationStatus.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun updatePlant(plant: Plant) {
        viewModelScope.launch {
            try {
                repository.updatePlant(plant)
                _operationStatus.value = OperationStatus.Success("Plant updated successfully")
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            try {
                repository.deletePlant(plant)
                _operationStatus.value = OperationStatus.Success("Plant deleted successfully")
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun scheduleReminder(plant: Plant) {
        try {
            val repeatInterval = plant.wateringFrequency.toLong()
            if (repeatInterval <= 0) {
                throw IllegalArgumentException("Watering frequency must be positive")
            }

            val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                repeatInterval = repeatInterval,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            ).build()
            
            WorkManager.getInstance(getApplication()).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun refreshPlants() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                // Trigger a refresh of the plants from the database
                repository.refreshPlants()
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

sealed class OperationStatus {
    data class Success(val message: String) : OperationStatus()
    data class Error(val message: String) : OperationStatus()
}