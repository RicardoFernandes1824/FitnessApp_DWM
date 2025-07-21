package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class Workout_Name : AppCompatActivity() {

    private lateinit var goBackButton: ImageButton
    private lateinit var startWorkoutButton: Button
    private lateinit var workoutRoutineName: TextView
    private lateinit var exercisesDisplay: RecyclerView

    data class WorkoutExerciseDisplay(
        val id: Int,
        val name: String,
        val sets: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workout_name_screen)

        // Initialize views
        goBackButton = findViewById(R.id.goBack_btn)
        startWorkoutButton = findViewById(R.id.startWorkout_btn)
        workoutRoutineName = findViewById(R.id.worktoutRoutineName)
        exercisesDisplay = findViewById(R.id.exercisesDisplay)
        exercisesDisplay.layoutManager = LinearLayoutManager(this)

        val workoutId = intent.getIntExtra("WORKOUT_ID", -1)
        Log.d("Workout_Name", "Received WORKOUT_ID: $workoutId")

        val deleteBtn: Button = findViewById(R.id.deleteTemplateBtn)
        deleteBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Workout Template")
                .setMessage("Are you sure you want to delete this Workout Routine? This action cannot be undone.")
                .setPositiveButton("Delete") { dialog, _ ->
                    dialog.dismiss()
                    if (workoutId != -1) {
                        deleteWorkoutTemplate(workoutId)
                    } else {
                        Toast.makeText(this, "Template ID not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        if (workoutId != -1) {
            fetchWorkoutRoutine(workoutId.toString())
        }

        goBackButton.setOnClickListener {
            val intent = Intent(this@Workout_Name, HomePage::class.java)
            startActivity(intent)
        }

        startWorkoutButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val userId = sharedPreferences.getString("userId", null)?.toIntOrNull() ?: -1
            Log.d("Workout_Name", "Start Workout pressed. TemplateId: $workoutId, UserId: $userId")
            Log.d("Workout_Name", "Workout title being passed: ${workoutRoutineName.text}")
            val intent = Intent(this, ActiveWorkoutActivity::class.java)
            intent.putExtra("TEMPLATE_ID", workoutId)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("WORKOUT_ID", workoutId) // Ensure WORKOUT_ID is included
            intent.putExtra("WORKOUT_NAME", workoutRoutineName.text.toString())
            startActivity(intent)
        }
    }

    private fun fetchWorkoutRoutine(workoutId: String) {
        val client = OkHttpClient()
        // Get userId from SharedPreferences as String and convert to Int
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userIdString = sharedPreferences.getString("userId", null)
        if (userIdString == null) {
            Log.i("WorkoutRoutine", "No userId found in SharedPreferences")
            return
        }
        val userId = userIdString.toIntOrNull()
        if (userId == null) {
            Log.i("WorkoutRoutine", "userId in SharedPreferences is not a valid integer")
            return
        }
        val url = "http://10.0.2.2:8080/user/$userId/workoutRoutine/$workoutId"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("WorkoutRoutine", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("WorkoutRoutine", "Response Body: $responseBody")
                if (!response.isSuccessful) {
                    Log.i("WorkoutRoutine", "Error: $response")
                } else {
                    if (responseBody != null) {
                        try {
                            val workout = JSONObject(responseBody)
                            val workoutName = workout.optString("name", "Unknown Workout")
                            val exercisesArray = workout.optJSONArray("workoutRoutineExercise") ?: JSONArray()
                            val exerciseDisplays = mutableListOf<WorkoutExerciseDisplay>()
                            for (j in 0 until exercisesArray.length()) {
                                val exerciseObj = exercisesArray.getJSONObject(j)
                                val exercise = exerciseObj.optJSONObject("exercise")
                                val exerciseId = exercise?.optInt("id", -1) ?: -1
                                val exerciseName = exercise?.optString("name", "Unknown Exercise") ?: "Unknown Exercise"
                                Log.d("WorkoutRoutine", "Parsed exerciseId: $exerciseId for $exerciseName")
                                val sets = exerciseObj.optInt("sets", 0)
                                exerciseDisplays.add(
                                    WorkoutExerciseDisplay(
                                        id = exerciseId,
                                        name = exerciseName,
                                        sets = sets
                                    )
                                )
                            }
                            Log.d("WorkoutRoutine", "Parsed exerciseDisplays: $exerciseDisplays")
                            Log.d("WorkoutRoutine", "Parsed workoutName: $workoutName")
                            Log.d("WorkoutRoutine", "Parsed exerciseDisplays: $exerciseDisplays")
                            runOnUiThread {
                                Log.d("WorkoutRoutine", "Updating UI with workoutName and exerciseDisplays")
                                workoutRoutineName.text = workoutName
                                Log.d("Workout_Name", "workoutRoutineName.text set to: $workoutName")
                                exercisesDisplay.adapter = WorkoutExerciseDisplayAdapter(exerciseDisplays)
                            }
                        } catch (e: JSONException) {
                            Log.i("WorkoutRoutine", "JSON Parsing error: ${'$'}{e.message}")
                        }
                    } else {
                        Log.i("WorkoutRoutine", "Response null")
                    }
                }
            }
        })
    }

    private fun deleteWorkoutTemplate(templateId: Int) {
        val url = "http://10.0.2.2:8080/workoutTemplate/$templateId"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Workout_Name", "Delete failed: ${'$'}{e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@Workout_Name, "Failed to delete template", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("DeleteTemplate", "Workout template delete response: code=${response.code}, body=$body")
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@Workout_Name, "Template deleted", Toast.LENGTH_SHORT).show()
                        // Navigate to HomePage and select Workout tab
                        val intent = Intent(this@Workout_Name, HomePage::class.java)
                        intent.putExtra("SELECTED_TAB", "workout")
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Workout_Name, "Failed to delete template", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}

// Adapter for displaying exercise cards with image, name, and sets count
class WorkoutExerciseDisplayAdapter(private val items: List<Workout_Name.WorkoutExerciseDisplay>) :
    RecyclerView.Adapter<WorkoutExerciseDisplayAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: android.widget.TextView = view.findViewById(R.id.exerciseName)
        val setsCount: android.widget.TextView = view.findViewById(R.id.setsCount)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_name_exercise, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.exerciseName.text = item.name
        holder.setsCount.text = "Sets: ${item.sets}"
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ExerciseGuideActivity::class.java)
            intent.putExtra("EXERCISE_ID", item.id)
            context.startActivity(intent)
        }
    }
    override fun getItemCount() = items.size
}
