package com.example.fitnessapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.stats_adapters.WorkoutRoutineStatsAdapter
import com.example.fitnessapp.stats_adapters.ExerciseStatsAdapter
import com.example.fitnessapp.workout_routine.WorkoutRoutine
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.fitnessapp.workout_routine.workoutRoutineList
import com.example.fitnessapp.exercise.exerciseList
import com.example.fitnessapp.exercise.Exercise
import android.widget.ImageButton

class StatsActivity : AppCompatActivity() {
    private lateinit var statsTitle: TextView
    private lateinit var searchBar: EditText
    private lateinit var recyclerView: RecyclerView
    private var allRoutines: List<WorkoutRoutine> = listOf()
    private var allExercises: List<Exercise> = listOf()
    private var routinesAdapter: WorkoutRoutineStatsAdapter? = null
    private var exercisesAdapter: ExerciseStatsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        statsTitle = findViewById(R.id.statsTitle)
        searchBar = findViewById(R.id.searchBar)
        recyclerView = findViewById(R.id.statsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Go back button logic
        val goBackBtn = findViewById<ImageButton>(R.id.goBackBtn)
        goBackBtn.setOnClickListener { finish() }

        val statsType = intent.getStringExtra("STATS_TYPE")
        if (statsType == "sessions") {
            statsTitle.text = "Sessions Stats"
            lifecycleScope.launch {
                allRoutines = workoutRoutineList(this@StatsActivity)
                routinesAdapter = WorkoutRoutineStatsAdapter(allRoutines) {}
                recyclerView.adapter = routinesAdapter
            }
        } else {
            statsTitle.text = "Exercise Stats"
            lifecycleScope.launch {
                allExercises = exerciseList(this@StatsActivity)
                exercisesAdapter = ExerciseStatsAdapter(allExercises) {}
                recyclerView.adapter = exercisesAdapter
            }
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString(), statsType)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filter(query: String, statsType: String?) {
        if (statsType == "sessions") {
            val filtered = allRoutines.filter {
                it.name.contains(query, ignoreCase = true)
            }
            routinesAdapter?.updateList(filtered)
        } else {
            val filtered = allExercises.filter {
                it.name.contains(query, ignoreCase = true)
            }
            exercisesAdapter?.updateList(filtered)
        }
    }
} 