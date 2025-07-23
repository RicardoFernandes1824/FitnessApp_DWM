package com.example.fitnessapp.exercise

import java.io.Serializable

// Data model for an Exercise

data class Exercise(
    val id: Int,
    val name: String,
    val description: String = "",
    val imageURL: String? = null,
    val category: String = ""
) : Serializable 