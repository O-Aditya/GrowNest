package com.adityacode.grownest.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.adityacode.grownest.data.DatabaseHelper
import com.adityacode.grownest.data.Plant
import com.adityacode.grownest.data.PlantRepository
import com.adityacode.grownest.databinding.ActivityAddPlantBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.viewModels

class AddPlantActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlantBinding
    private val viewModel: PlantViewModel by viewModels()
    private var imageUri: Uri? = null
    
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            binding.plantImage.setImageURI(imageUri)
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            binding.plantImage.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupImagePicker()
        setupSaveButton()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupImagePicker() {
        binding.btnAddImage.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Plant Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> pickFromGallery()
                }
            }
            .show()
    }

    private fun takePhoto() {
        val photoFile = createImageFile()
        imageUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            photoFile
        )
        takePicture.launch(imageUri)
    }

    private fun pickFromGallery() {
        pickImage.launch("image/*")
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            savePlant()
        }
    }

    private fun savePlant() {
        val name = binding.plantName.text.toString()
        val species = binding.plantSpecies.text.toString()
        val wateringFrequency = binding.wateringFrequency.text.toString().toIntOrNull() ?: 0
        
        Log.d("AddPlantActivity", "Saving plant: $name, $species, $wateringFrequency")
        
        val plant = Plant(
            name = name,
            species = species,
            wateringFrequency = wateringFrequency,
            // lastWatered and imageUrl will use default values
        )

        viewModel.addPlant(plant)
        finish()
    }

    private fun observeViewModel() {
        viewModel.operationStatus.observe(this) { status ->
            when (status) {
                is OperationStatus.Success -> {
                    showToast(status.message)
                    finish()
                }
                is OperationStatus.Error -> {
                    showToast("Error: ${status.message}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}