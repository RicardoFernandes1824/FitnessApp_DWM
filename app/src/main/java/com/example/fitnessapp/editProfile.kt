package com.example.fitnessapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class editProfile : AppCompatActivity() {
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var saveChangesButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.edit_profile)

        firstNameInput = findViewById(R.id.firstNameEdit)
        lastNameInput = findViewById(R.id.lastNameEdit)
        heightInput = findViewById(R.id.heightEdit)
        weightInput = findViewById(R.id.weightEdit)
        genderSpinner = findViewById(R.id.spinnerEdit)
        saveChangesButton = findViewById(R.id.saveChangesBtn)

        // Populate the Spinner with gender options
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        // Retrieve the username from SharedPreferences
        val sharedPreferences =
            getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "").orEmpty()
        val token = sharedPreferences.getString("token", "").orEmpty()
        val username = sharedPreferences.getString("username", "").orEmpty()
        val firstName = sharedPreferences.getString("firstName","").orEmpty()
        val lastName = sharedPreferences.getString("lastName","").orEmpty()

        val usernameEditTxt: TextView = findViewById(R.id.usernameEditTxt)
        usernameEditTxt.text = "$username"

        val fullName: TextView = findViewById(R.id.fullName)
        fullName.text = "$firstName $lastName"

        // Handle sign-up button click
        saveChangesButton.setOnClickListener {
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val heightStr = heightInput.text.toString()
            val weightStr = weightInput.text.toString()
            val gender = genderSpinner.selectedItem.toString()


            try {
                val height = heightStr.toInt() // Convert String to Int
                val weight = weightStr.toInt() // Convert String to Int
                patchUpdateUserRequestOkHttp(userId, firstName, lastName, gender, height, weight,token)

            } catch (e: NumberFormatException) {
                Log.i("UpdateClient", "Height and Weight must be valid integers.")
                return@setOnClickListener
            }
        }
    }

    private fun patchUpdateUserRequestOkHttp(
        userId: String,
        firstName: String,
        lastName: String,
        gender: String,
        height: Int,
        weight: Int,
        token: String
    ) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/users/$userId"

        // Create JSON request body
        val jsonBody = """
        {
            "firstName": "$firstName",
            "lastName": "$lastName",
            "gender": "$gender",
            "height": "$height",
            "weight": "$weight"
        }
        """
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        // Build the request
        val request = Request.Builder()
            .url(url)
            .method("PATCH",requestBody)
            .addHeader("Authorization", "Bearer $token")
            .build()

        // Send the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.i("UpdateClient", "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Log.i("UpdateClient", "Update successful: $responseBody")

                        // Navigate to CreateProfile
                        runOnUiThread {
                            val intent = Intent(this@editProfile, Settings::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.i("UpdateClient", "UpdateClient failed with code:${response.code} ")
                    }
                }
            }
        })
    }
}
