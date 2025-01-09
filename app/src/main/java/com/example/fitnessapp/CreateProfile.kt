package com.example.fitnessapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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


class CreateProfile : AppCompatActivity() {

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.create_profile)

        firstNameInput = findViewById(R.id.firstName)
        lastNameInput = findViewById(R.id.lastName)
        heightInput = findViewById(R.id.height)
        weightInput = findViewById(R.id.kilograms)
        genderSpinner = findViewById(R.id.gender)
        nextButton = findViewById(R.id.create_profile_btn)

        // Populate the Spinner with gender options
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        // Handle sign-up button click
        nextButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val height = heightInput.text.toString().trim()
            val weight = weightInput.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()

            // Validate input
            if (firstName.isEmpty() || lastName.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                Log.i("UpdateClient", "All fields are required to be completed.")
                return@setOnClickListener
            }

            try {
                // Convert height and weight inputs to integers
                val height = height.toInt()
                val weight = weight.toInt()
                patchUpdateUserRequestOkHttp(firstName, lastName, gender, height, weight)

            } catch (e: NumberFormatException) {
                Log.i("UpdateClient", "Height and Weight must be valid integers.")
                return@setOnClickListener
            }
        }
    }

        private fun patchUpdateUserRequestOkHttp(
            firstName: String,
            lastName: String,
            gender: String,
            height: Int,
            weight: Int
        ) {
            val client = OkHttpClient()
            val url = "http://10.0.2.2:8080/users/:id"

            // Create JSON request body
            val jsonBody = """
        {
            "firstNameInput": "$firstName",
            "lastNameInput": "$lastName",
            "gender": "$gender",
            "heightInput": "$height",
            "weightInput": "$weight"
        }
        """
            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            // Build the request
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
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
                                val intent = Intent(this@CreateProfile, HomePage::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Log.i("UpdateClient", "UpdateClient failed with code: ${response.code}")
                        }
                    }
                }
            })
        }
    }


