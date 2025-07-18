package com.example.fitnessapp.exercise

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

suspend fun exerciseList(context: Context): List<Exercise> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://10.0.2.2:8080/exercise")
        .build()

    try {
        Log.d("ExerciseAPI", "Sending request to http://10.0.2.2:8080/exercise")
        val response = client.newCall(request).execute()
        Log.d("ExerciseAPI", "Response code: ${response.code}")
        if (response.isSuccessful) {
            val body = response.body?.string()
            Log.d("ExerciseAPI", "Response body: $body")
            if (body != null) {
                val type = object : TypeToken<List<Exercise>>() {}.type
                val exercises = Gson().fromJson<List<Exercise>>(body, type)
                Log.d("ExerciseAPI", "Parsed exercises: $exercises")
                exercises
            } else {
                Log.w("ExerciseAPI", "Response body is null")
                emptyList()
            }
        } else {
            Log.w("ExerciseAPI", "Unsuccessful response: ${response.code}")
            emptyList()
        }
    } catch (e: Exception) {
        Log.e("ExerciseAPI", "Exception: $e")
        e.printStackTrace()
        emptyList()
    }
} 