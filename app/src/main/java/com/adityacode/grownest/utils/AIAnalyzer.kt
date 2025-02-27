package com.adityacode.grownest.utils

object AIAnalyzer {
    fun analyzePlantHealth(imageUri: String?): String {
        return if (imageUri != null) "Healthy plant detected" else "No image available for analysis"
    }
}