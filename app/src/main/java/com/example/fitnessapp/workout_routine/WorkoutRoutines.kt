package com.example.fitnessapp.workout_routine

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

suspend fun workoutRoutineList(context: Context): List<WorkoutRoutine> = withContext(Dispatchers.IO) {
    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "")?.toIntOrNull() ?: return@withContext emptyList()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://10.0.2.2:8080/workoutRoutine/$userId")
        .build()

    try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            response.body?.string()?.let { jsonString ->
                val gson = Gson()
                val type = object : TypeToken<List<WorkoutRoutine>>() {}.type
                gson.fromJson<List<WorkoutRoutine>>(jsonString, type)
            } ?: emptyList()
        } else {
            emptyList() // Handle HTTP error response
        }
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList() // Handle network error
    }
}
