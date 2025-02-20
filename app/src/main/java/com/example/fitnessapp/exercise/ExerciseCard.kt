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

suspend fun exerciseList(context: Context, client: OkHttpClient = OkHttpClient()): List<Exercise> =
    withContext(Dispatchers.IO) {

        val request = Request.Builder()
            .url("http://10.0.2.2:8080/exercise") // Change to proper API URL
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    Log.d("exerciseList", "API Response: $jsonString") // Debugging log

                    jsonString?.let {
                        val gson = Gson()
                        val type = object : TypeToken<List<Exercise>>() {}.type
                        return@withContext gson.fromJson(it, type)
                    } ?: emptyList()
                } else {
                    Log.e("exerciseList", "HTTP Error: ${response.code}")
                    emptyList()
                }
            }
        } catch (e: IOException) {
            Log.e("exerciseList", "Network Error: ${e.message}")
            emptyList()
        }

    }
