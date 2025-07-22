package com.example.fitnessapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import android.widget.ImageButton

class SessionDetailActivity : AppCompatActivity() {
    private lateinit var sessionName: TextView
    private lateinit var exercisesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        sessionName = findViewById(R.id.sessionDetailWorkoutName)
        exercisesContainer = findViewById(R.id.sessionDetailExercisesContainer)

        findViewById<ImageButton>(R.id.goBackBtn).setOnClickListener { finish() }

        val sessionId = intent.getIntExtra("SESSION_ID", -1)
        if (sessionId != -1) {
            fetchSessionDetail(sessionId)
        }
    }

    private fun fetchSessionDetail(sessionId: Int) {
        val url = "http://10.0.2.2:8080/workoutSession/$sessionId"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SessionDetail", "Failed to fetch session", e)
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val obj = JSONObject(body)
                    val workoutName = obj.optJSONObject("workout")?.optString("name") ?: "-"
                    val setsArr = obj.optJSONArray("trainningSessionSet") ?: JSONArray()
                    // Group sets by exercise name
                    val setsByExercise = mutableMapOf<String, MutableList<JSONObject>>()
                    for (i in 0 until setsArr.length()) {
                        val setObj = setsArr.getJSONObject(i)
                        val exName = setObj.optJSONObject("exercise")?.optString("name") ?: "-"
                        setsByExercise.getOrPut(exName) { mutableListOf() }.add(setObj)
                    }
                    runOnUiThread {
                        sessionName.text = workoutName
                        exercisesContainer.removeAllViews()
                        val inflater = LayoutInflater.from(this@SessionDetailActivity)
                        for ((exerciseName, setList) in setsByExercise) {
                            // Inflate a static card for each exercise
                            val cardView = inflater.inflate(R.layout.item_session_exercise_detail, exercisesContainer, false)
                            // Set exercise name
                            val exNameView = cardView.findViewById<TextView>(R.id.sessionExerciseName)
                            exNameView.text = exerciseName
                            // Set exercise image
                            val imageView = cardView.findViewById<ImageView>(R.id.sessionExerciseImage)
                            when (exerciseName.lowercase()) {
                                "chest press", "incline bench press", "bench press" -> imageView.setImageResource(R.drawable.icon_chest)
                                "squat", "leg press", "hack squat" -> imageView.setImageResource(R.drawable.icon_legs)
                                "shoulder press", "overhead press" -> imageView.setImageResource(R.drawable.icon_chest)
                                "deadlift", "row", "lat pulldown" -> imageView.setImageResource(R.drawable.icon_back)
                                else -> imageView.setImageResource(R.drawable.dumbbell)
                            }
                            // Fill sets table
                            val setsContainer = cardView.findViewById<LinearLayout>(R.id.sessionExerciseSetsContainer)
                            for ((index, setObj) in setList.withIndex()) {
                                val setRow = inflater.inflate(R.layout.item_active_set, setsContainer, false)
                                // Set number (row index + 1)
                                setRow.findViewById<TextView>(R.id.setNumber).text = (index + 1).toString()
                                // Set weight
                                val weightView = setRow.findViewById<EditText>(R.id.weightInput)
                                weightView.setText(setObj.optInt("weight", 0).toString())
                                weightView.isEnabled = false
                                weightView.setTextColor(android.graphics.Color.BLACK)
                                // Set reps
                                val repsView = setRow.findViewById<EditText>(R.id.repsInput)
                                repsView.setText(setObj.optInt("reps", 0).toString())
                                repsView.isEnabled = false
                                repsView.setTextColor(android.graphics.Color.BLACK)
                                // Hide previousValue and confirmSetBtn
                                setRow.findViewById<TextView>(R.id.previousValue).visibility = android.view.View.GONE
                                setRow.findViewById<android.widget.ImageButton>(R.id.confirmSetBtn).visibility = android.view.View.GONE
                                setsContainer.addView(setRow)
                            }
                            exercisesContainer.addView(cardView)
                        }
                    }
                }
            }
        })
    }
} 