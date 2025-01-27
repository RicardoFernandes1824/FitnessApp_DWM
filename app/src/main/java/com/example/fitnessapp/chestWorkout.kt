package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ChestWorkout : AppCompatActivity() {

    private lateinit var goBackButton: ImageButton
    private lateinit var startWorkoutButton: Button
    private lateinit var workoutRoutineName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chest_workout)

        // Initialize views
        goBackButton = findViewById(R.id.goBack_btn)
        startWorkoutButton = findViewById(R.id.startWorkout_btn)
        workoutRoutineName = findViewById(R.id.worktoutRoutineName) // Ensure this matches your layout ID

        // Testar
        fetchWorkoutRoutine("1") // Replace "1" with the desired ID

        // Set up listeners
        goBackButton.setOnClickListener {
            val intent = Intent(this@ChestWorkout, HomePage::class.java)
            startActivity(intent)
        }
    }

    private fun fetchWorkoutRoutine(id: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/workoutRoutine/$id"

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
                    if (responseBody != null) {
                        try {
                            val jsonObject = JSONObject(responseBody)
                            val workoutName = jsonObject.getString("name")

                            runOnUiThread {
                                workoutRoutineName.text = workoutName
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
