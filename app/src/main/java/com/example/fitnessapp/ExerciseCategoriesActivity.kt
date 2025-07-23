package com.example.fitnessapp

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.exercise.Exercise
import com.example.fitnessapp.exercise.exerciseList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExerciseCategoriesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private lateinit var adapter: ExerciseCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_categories)

        recyclerView = findViewById(R.id.recycler_exercise_categories)
        backButton = findViewById(R.id.backButton)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ExerciseCategoryAdapter { exercise ->
            val intent = android.content.Intent(this, ExerciseGuideActivity::class.java)
            intent.putExtra("EXERCISE_ID", exercise.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        backButton.setOnClickListener { finish() }

        CoroutineScope(Dispatchers.Main).launch {
            val exercises = withContext(Dispatchers.IO) { exerciseList(this@ExerciseCategoriesActivity) }
            adapter.submitList(exercises)
        }
    }
} 