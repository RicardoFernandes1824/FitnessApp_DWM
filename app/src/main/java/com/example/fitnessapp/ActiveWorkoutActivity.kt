package com.example.fitnessapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

// Data classes
data class ActiveExercise(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val sets: MutableList<ActiveSet>
)
data class ActiveSet(
    val id: Int? = null,
    val setNumber: Int,
    val previousWeight: Int?,
    val previousReps: Int?,
    var weight: Int,
    var reps: Int,
    var done: Boolean = false,
    val templateReps: Int? = null,
    val templateWeight: Int? = null
)

class ActiveWorkoutActivity : AppCompatActivity() {

    private lateinit var exercisesRecyclerView: RecyclerView
    private lateinit var finishWorkoutBtn: Button
    private lateinit var adapter: ActiveExerciseAdapter
    private val exercises = mutableListOf<ActiveExercise>()
    private var sessionId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_workout)

        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView)
        finishWorkoutBtn = findViewById(R.id.finishWorkoutBtn)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ActiveExerciseAdapter(exercises)
        exercisesRecyclerView.adapter = adapter

        val goBackBtn = findViewById<android.widget.ImageButton>(R.id.goBackBtn)
        val workoutTitle = findViewById<android.widget.TextView>(R.id.activeWorkoutTitle)
        workoutTitle.text = "Workout" // Default title
        goBackBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cancel Workout?")
                .setMessage("Are you sure you want to cancel this workout? All progress will be lost.")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        val templateId = intent.getIntExtra("TEMPLATE_ID", -1)
        val userId = intent.getIntExtra("USER_ID", -1)
        if (templateId == -1 || userId == -1) {
            Toast.makeText(this, "Invalid workout or user", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        createSessionAndLoadData(templateId, userId, workoutTitle)

        finishWorkoutBtn.setOnClickListener {
            saveSessionResultsAndShowSummary()
        }
    }

    private fun createSessionAndLoadData(templateId: Int, userId: Int, workoutTitle: android.widget.TextView? = null) {
        val url = "http://10.0.2.2:8080/workoutSession/fromTemplate/$templateId/$userId"
        val client = OkHttpClient()
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("authToken", null)
        val requestBuilder = Request.Builder().url(url).post(RequestBody.create(null, ByteArray(0)))
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        val request = requestBuilder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ActiveWorkoutActivity, "Failed to load session", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("ActiveWorkout", "Session response: $body")
                if (response.isSuccessful && body != null) {
                    val json = JSONObject(body)
                    sessionId = json.optInt("sessionId", -1).takeIf { it != -1 }
                    val exercisesJson = json.getJSONArray("exercises")
                    val setsJson = json.getJSONArray("sets")
                    val exerciseList = mutableListOf<ActiveExercise>()
                    for (i in 0 until exercisesJson.length()) {
                        val exObj = exercisesJson.getJSONObject(i)
                        val exId = exObj.getInt("id")
                        val exName = exObj.getString("name")
                        val exImage = exObj.optString("imageUrl", null)
                        // Find all sets for this exercise
                        val setsList = mutableListOf<ActiveSet>()
                        for (j in 0 until setsJson.length()) {
                            val setObj = setsJson.getJSONObject(j)
                            if (setObj.getInt("exerciseId") == exId) {
                                setsList.add(
                                    ActiveSet(
                                        id = setObj.optInt("id", -1).takeIf { it != -1 },
                                        setNumber = setObj.getInt("setNumber"),
                                        previousWeight = if (setObj.isNull("previousWeight")) null else setObj.getInt("previousWeight"),
                                        previousReps = if (setObj.isNull("previousReps")) null else setObj.getInt("previousReps"),
                                        weight = 0, // Start empty for user input
                                        reps = 0, // Start empty for user input
                                        done = false,
                                        templateReps = setObj.optInt("reps", 0),
                                        templateWeight = setObj.optInt("weight", 0)
                                    )
                                )
                            }
                        }
                        exerciseList.add(ActiveExercise(exId, exName, exImage, setsList))
                    }
                    Log.d("ActiveWorkout", "Parsed exercises: $exerciseList")
                    runOnUiThread {
                        exercises.clear()
                        exercises.addAll(exerciseList)
                        adapter.notifyDataSetChanged()
                        // Set the workout title to the first exercise's name or a default
                        workoutTitle?.text = if (exerciseList.isNotEmpty()) exerciseList[0].name else "Workout"
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ActiveWorkoutActivity, "Session creation failed: ${body ?: response.code}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun saveSessionResultsAndShowSummary() {
        // Collect all set results
        val setsArray = JSONArray()
        for (ex in exercises) {
            for (set in ex.sets) {
                if (set.id != null) {
                    val setObj = JSONObject()
                    setObj.put("id", set.id)
                    setObj.put("weight", set.weight)
                    setObj.put("reps", set.reps)
                    setsArray.put(setObj)
                }
            }
        }
        val json = JSONObject()
        if (sessionId != null) json.put("sessionId", sessionId)
        json.put("sets", setsArray)
        val url = "http://10.0.2.2:8080/workoutSession/saveResults"
        val client = OkHttpClient()
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ActiveWorkoutActivity, "Failed to save results", Toast.LENGTH_SHORT).show()
                    showSummaryDialog()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    showSummaryDialog()
                }
            }
        })
    }

    private fun showSummaryDialog() {
        val completedSets = exercises.sumOf { ex -> ex.sets.count { it.done } }
        val totalSets = exercises.sumOf { it.sets.size }
        AlertDialog.Builder(this)
            .setTitle("Workout Complete")
            .setMessage("You completed $completedSets out of $totalSets sets!\nGreat job!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }
} 