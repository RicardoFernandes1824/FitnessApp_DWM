package com.example.fitnessapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class Login : AppCompatActivity() {

    // Declare views and SharedPreferences
    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginButton: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var rememberMeCheckbox: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        // Initialize views
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_btn)
        rememberMeCheckbox = findViewById(R.id.checkBox)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                Log.i("Login", "Please enter both username and password")
                return@setOnClickListener
            }

            postLoginRequestOkHttp(username, password)
        }
    }

    // Function to handle login request
    private fun postLoginRequestOkHttp(username: String, password: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/login"

        // Create JSON request body
        val jsonBody = """
        {
            "username": "$username",
            "password": "$password"
        }
        """
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.i("Login", "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    val token = extractTokenFromResponse(responseBody)
                    val userId = extractUserIdFromResponse(responseBody)
                    val firstName = extractFirstNameFromResponse(responseBody)
                    val lastName = extractLastNameFromResponse(responseBody)

                    val editor = sharedPreferences.edit()
                    editor.putString("username", username)
                    editor.putString("userId", userId)
                    editor.putString("token", token)
                    editor.putString("firstName", firstName)
                    editor.putString("lastName", lastName)
                    editor.putBoolean("rememberMe", rememberMeCheckbox.isChecked)
                    editor.apply()

                    if (firstName == "null" || firstName == "") {
                        val intent = Intent(this@Login, CreateProfile::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@Login, HomePage::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.i("Login", "Login failed: ${response.code}")
                }
            }
        })
    }
}

private fun extractTokenFromResponse(responseBody: String?): String {
    return try {
        val jsonObject = org.json.JSONObject(responseBody ?: "")
        jsonObject.getString("token")
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

private fun extractFirstNameFromResponse(responseBody: String?): String? {
    return try {
        val jsonObject = org.json.JSONObject(responseBody ?: "")
        jsonObject.optString("firstName", "null")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun extractLastNameFromResponse(responseBody: String?): String? {
    return try {
        val jsonObject = org.json.JSONObject(responseBody ?: "")
        jsonObject.optString("lastName", "null")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun extractUserIdFromResponse(responseBody: String?): String? {
    return try {
        val jsonObject = org.json.JSONObject(responseBody ?: "")
        jsonObject.getString("userId")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
