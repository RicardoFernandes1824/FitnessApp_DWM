package com.example.fitnessapp.exercise

import java.io.Serializable

data class WorkoutExercise(
    val exercise: Exercise,
    val sets: MutableList<ExerciseSet> = mutableListOf()
) : Serializable 