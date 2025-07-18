package com.example.fitnessapp.exercise

import java.io.Serializable

data class ExerciseSet(
    var setNumber: Int,
    var previousWeight: String = "",
    var weight: String = "",
    var reps: String = "",
    var objectiveReps: String = "10"
) : Serializable 