package com.example.fitnessapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.exercise.Exercise
import com.example.fitnessapp.exercise.WorkoutExercise
import com.example.fitnessapp.exercise.ExerciseSet
import com.example.fitnessapp.exercise.WorkoutExerciseTableAdapter
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import android.util.Log

class CreateWorkout : AppCompatActivity() {

    private lateinit var goBackButton: ImageButton
    private lateinit var addExerciseBtn: Button
    private lateinit var exerciseTableRecyclerView: RecyclerView
    private lateinit var workoutExerciseTableAdapter: WorkoutExerciseTableAdapter
    private var workoutExercises: MutableList<WorkoutExercise> = mutableListOf()

    companion object {
        private const val REQUEST_ADD_EXERCISES = 1001
        const val EXTRA_SELECTED_EXERCISES = "selected_exercises"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_workout_screen)

        goBackButton = findViewById(R.id.goBackCreateWorkout)
        addExerciseBtn = findViewById(R.id.addExercise)
        exerciseTableRecyclerView = findViewById(R.id.exerciseTableRecyclerView)
        val noExerciseAdded = findViewById<View>(R.id.noExerciseAdded)
        val saveButton = findViewById<Button>(R.id.saveWorkoutRoutine)

        goBackButton.setOnClickListener { finish() }

        addExerciseBtn.setOnClickListener {
            val selectedExercises = workoutExercises.map { it.exercise }
            val intent = Intent(this@CreateWorkout, AddExercises::class.java)
            intent.putExtra(EXTRA_SELECTED_EXERCISES, ArrayList(selectedExercises))
            startActivityForResult(intent, REQUEST_ADD_EXERCISES)
        }

        saveButton.setOnClickListener {
            Log.d("WorkoutSave", "Save button clicked")
            Toast.makeText(this, "Save clicked", Toast.LENGTH_SHORT).show()
            val workoutNameEditText = findViewById<EditText>(R.id.workoutRoutineName)
            val workoutName = workoutNameEditText.text.toString().trim()
            if (workoutName.isEmpty()) {
                workoutNameEditText.error = "Workout name is required"
                workoutNameEditText.requestFocus()
                return@setOnClickListener
            }
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val userId = sharedPreferences.getString("userId", null)?.toIntOrNull() ?: -1
            val video = ""

            val workoutRoutineExercise = workoutExercises.map { ex ->
                mapOf("id" to ex.exercise.id)
            }
            val workoutRoutineTemplate = workoutExercises.flatMap { ex ->
                ex.sets.map { set ->
                    mapOf(
                        "reps" to (set.reps.toIntOrNull() ?: 0),
                        "sets" to 1, // Each set is a single entry
                        "exerciseId" to ex.exercise.id
                    )
                }
            }

            val bodyMap = mapOf(
                "name" to workoutName,
                "video" to video,
                "workoutRoutineExercise" to workoutRoutineExercise,
                "workoutRoutineTemplate" to workoutRoutineTemplate,
                "user" to userId
            )

            val json = Gson().toJson(bodyMap)
            Log.d("WorkoutSave", "Request JSON: $json")
            val client = OkHttpClient()
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/workoutRoutine")
                .post(requestBody)
                .build()

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val response = client.newCall(request).execute()
                        val responseBody = response.body?.string()
                        Log.d("WorkoutSave", "Response code: ${response.code}")
                        Log.d("WorkoutSave", "Response body: $responseBody")
                        if (response.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(this@CreateWorkout, "Workout saved!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@CreateWorkout, HomePage::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.putExtra("SELECTED_TAB", "workout")
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@CreateWorkout, "Failed to save workout", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("WorkoutSave", "Exception: $e")
                        runOnUiThread {
                            Toast.makeText(this@CreateWorkout, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        workoutExerciseTableAdapter = WorkoutExerciseTableAdapter(workoutExercises) { workoutExercise ->
            val index = workoutExercises.indexOf(workoutExercise)
            if (index != -1) {
                workoutExercises.removeAt(index)
                workoutExerciseTableAdapter.notifyItemRemoved(index)
                updateTableVisibility(findViewById(R.id.noExerciseAdded))
            }
        }
        exerciseTableRecyclerView.layoutManager = LinearLayoutManager(this)
        exerciseTableRecyclerView.adapter = workoutExerciseTableAdapter

        // Initial visibility
        updateTableVisibility(noExerciseAdded)
    }

    private fun updateTableVisibility(noExerciseAdded: View) {
        if (workoutExercises.isEmpty()) {
            noExerciseAdded.visibility = View.VISIBLE
            exerciseTableRecyclerView.visibility = View.GONE
        } else {
            noExerciseAdded.visibility = View.GONE
            exerciseTableRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val noExerciseAdded = findViewById<View>(R.id.noExerciseAdded)
        if (requestCode == REQUEST_ADD_EXERCISES && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val selectedExercises = data.getSerializableExtra(EXTRA_SELECTED_EXERCISES) as? ArrayList<Exercise>
                if (selectedExercises != null) {
                    // Update workoutExercises, preserving sets if possible
                    val newWorkoutExercises = mutableListOf<WorkoutExercise>()
                    for (exercise in selectedExercises) {
                        val existing = workoutExercises.find { it.exercise.id == exercise.id }
                        if (existing != null) {
                            newWorkoutExercises.add(existing)
                        } else {
                            val sets = mutableListOf<ExerciseSet>()
                            sets.add(ExerciseSet(setNumber = 1))
                            newWorkoutExercises.add(WorkoutExercise(exercise, sets))
                        }
                    }
                    workoutExercises.clear()
                    workoutExercises.addAll(newWorkoutExercises)
                    // Always re-create the adapter with the remove callback
                    workoutExerciseTableAdapter = WorkoutExerciseTableAdapter(workoutExercises) { workoutExercise ->
                        val index = workoutExercises.indexOf(workoutExercise)
                        if (index != -1) {
                            workoutExercises.removeAt(index)
                            workoutExerciseTableAdapter.notifyItemRemoved(index)
                            updateTableVisibility(findViewById(R.id.noExerciseAdded))
                        }
                    }
                    exerciseTableRecyclerView.adapter = workoutExerciseTableAdapter
                    updateTableVisibility(noExerciseAdded)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}