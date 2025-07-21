package com.example.fitnessapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.CreateWorkout.Companion.EXTRA_SELECTED_EXERCISES
import com.example.fitnessapp.exercise.Exercise
import com.example.fitnessapp.exercise.ExerciseAdapter
import com.example.fitnessapp.exercise.exerciseList
import kotlinx.coroutines.launch

class AddExercises : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseAdapter
    private lateinit var goBackButton: ImageButton
    private lateinit var addExerciseBtn: Button
    private lateinit var searchEditText: EditText
    private var allExercises: List<Exercise> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_exercises_screen)

        goBackButton = findViewById(R.id.goBackAddExercise)
        recyclerView = findViewById(R.id.recycler_add_exercise)
        addExerciseBtn = findViewById(R.id.addExerciseBtn)
        searchEditText = findViewById(R.id.editTextText)

        goBackButton.setOnClickListener { finish() }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExerciseAdapter { selectedCount ->
            addExerciseBtn.text = when (selectedCount) {
                0, 1 -> "Add Exercise"
                else -> "Add Exercises"
            }
            addExerciseBtn.isEnabled = selectedCount > 0
        }
        recyclerView.adapter = adapter

        addExerciseBtn.isEnabled = false
        addExerciseBtn.setOnClickListener {
            val selectedExercises = ArrayList(adapter.getSelectedExercises())
            val resultIntent = Intent().apply {
                putExtra(EXTRA_SELECTED_EXERCISES, selectedExercises)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Listen for text changes to filter
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterExercises() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Load exercises from API
        lifecycleScope.launch {
            val exercises = exerciseList(this@AddExercises) // Fetch API data
            runOnUiThread {
                allExercises = exercises
                adapter.submitList(exercises) // Ensure UI update happens
                // Restore previous selection if any
                val previouslySelected = intent.getSerializableExtra(EXTRA_SELECTED_EXERCISES) as? ArrayList<Exercise> ?: arrayListOf()
                adapter.setInitialSelection(previouslySelected)
            }
        }
    }

    private fun filterExercises() {
        val query = searchEditText.text.toString().trim().lowercase()
        val filtered = allExercises.filter { exercise ->
            query.isEmpty() ||
            exercise.name.lowercase().contains(query) ||
            exercise.category.lowercase().contains(query)
        }
        adapter.submitList(filtered)
    }
}
