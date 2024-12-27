package com.example.fitnessapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

class SignUp : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sign_up_screen)

        // Initialize views
        usernameInput = findViewById(R.id.username)
        emailInput = findViewById(R.id.email)
        passwordInput = findViewById(R.id.password)
        confirmPasswordInput = findViewById(R.id.confirmPassword)
        signUpButton = findViewById(R.id.signUpButton)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Handle sign-up button click
        signUpButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            // Validate input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Log.i("Register", "All fields are required to be completed.")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Log.i("Register", "Passwords do not match.")
                return@setOnClickListener
            }

            // Make sign-up request
            postSignInRequestOkHttp(username, email, password)
        }
    }

    // Function to handle sign-up request
    private fun postSignInRequestOkHttp(username: String, email: String, password: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/register"

        // Create JSON request body
        val jsonBody = """
        {
            "username": "$username",
            "email": "$email",
            "password": "$password"
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
                Log.i("Register", "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Log.i("Register", "Register successful: $responseBody")

                        // Save username to SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("username", username)
                        editor.apply()

                        // Navigate to CreateProfile
                        runOnUiThread {
                            val intent = Intent(this@SignUp, CreateProfile::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.i("Register", "Register failed with code: ${response.code}")
                    }
                }
            }
        })
    }
}
