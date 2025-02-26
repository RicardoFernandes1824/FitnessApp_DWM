package com.example.fitnessapp.exercise

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

suspend fun workoutRoutineList(exerciseId: Int, client: OkHttpClient = OkHttpClient()): List<WorkoutRoutineTemplate> =
    withContext(Dispatchers.IO) {

        val url = "http://10.0.2.2:8080/getWorkoutRoutineTemplateByExerciseId/$exerciseId"

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()?.let { jsonString ->
                        Log.d("API", "Response: $jsonString") // Debugging log
                        val gson = Gson()
                        val type = object : TypeToken<List<WorkoutRoutineTemplate>>() {}.type
                        return@withContext gson.fromJson(jsonString, type)
                    } ?: emptyList()
                } else {
                    Log.e("API", "HTTP Error: ${response.code}")
                    emptyList()
                }
            }
        } catch (e: IOException) {
            Log.e("API", "Network Error: ${e.message}")
            emptyList()
        }
    }
    }
