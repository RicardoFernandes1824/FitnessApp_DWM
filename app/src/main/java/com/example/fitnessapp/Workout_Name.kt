package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Workout_Name : AppCompatActivity() {

    private lateinit var goBackButton: ImageButton
    private lateinit var startWorkoutButton: Button
    private lateinit var workoutRoutineName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workout_name_screen)

        // Initialize views
        goBackButton = findViewById(R.id.goBack_btn)
        startWorkoutButton = findViewById(R.id.startWorkout_btn)
        workoutRoutineName = findViewById(R.id.worktoutRoutineName) // Ensure this matches your layout ID

        val workoutId = intent.getIntExtra("WORKOUT_ID", -1) // Gets the workout ID
        Log.d("Workout_Name", "Received WORKOUT_ID: $workoutId")

        if (workoutId != -1) {
            fetchWorkoutRoutine(workoutId.toString())
        }

        // Set up listeners
        goBackButton.setOnClickListener {
            val intent = Intent(this@Workout_Name, HomePage::class.java)
            startActivity(intent)
        }
    }

    private fun fetchWorkoutRoutine(workoutId: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/workoutRoutine/${workoutId}"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("WorkoutRoutine", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.i("WorkoutRoutine", "Error: $response")
                } else {
                    val responseBody = response.body?.string()
                    Log.d("WorkoutRoutine", "Response Body: $responseBody")
                    if (responseBody != null) {
                        try {
                            val jsonArray = JSONArray(responseBody) // Parse as JSONArray
                            // Loop through the array to find the workout by ID
                            val workoutIdToFind = workoutId.toInt() // Convert workoutId string to int
                            for (i in 0 until jsonArray.length()) {
                                val workout = jsonArray.getJSONObject(i)
                                val workoutID = workout.optInt("id")
                                if (workoutID == workoutIdToFind) {
                                    val workoutName = workout.optString("name", "Unknown Workout")
                                    runOnUiThread {
                                        workoutRoutineName.text = workoutName
                                    }
                                    break
                                }
                            }
                        } catch (e: JSONException) {
                            Log.i("WorkoutRoutine", "JSON Parsing error: ${e.message}")
                        }
                    } else {
                        Log.i("WorkoutRoutine", "Response null")
                    }
                }
            }
        })
    }
}
