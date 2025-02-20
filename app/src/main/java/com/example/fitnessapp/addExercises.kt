package com.example.fitnessapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.exercise.ExerciseAdapter
import com.example.fitnessapp.exercise.exerciseList
import kotlinx.coroutines.launch

class AddExercises : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseAdapter
    private lateinit var goBackButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_exercises_screen)

        goBackButton = findViewById(R.id.goBackAddExercise)
        recyclerView = findViewById(R.id.recycler_add_exercise)

        goBackButton.setOnClickListener { finish() }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExerciseAdapter { exercise ->
            Log.d("Exercise Clicked", "Clicked on: ${exercise.name}")
        }
        recyclerView.adapter = adapter

        // Load exercises from API
        lifecycleScope.launch {
            val exercises = exerciseList(this@AddExercises) // Fetch API data
            runOnUiThread {
                adapter.submitList(exercises) // Ensure UI update happens
            }
        }
    }
}
