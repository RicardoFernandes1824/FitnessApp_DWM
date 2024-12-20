package com.example.fitnessapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitnessapp.databinding.MainActivityBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class Login : AppCompatActivity() {

    lateinit var username_input : EditText
    lateinit var password_input : EditText
    lateinit var login_btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        var client = OkHttpClient()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


       username_input = findViewById(R.id.username_input)
       password_input = findViewById(R.id.password_input)
       login_btn = findViewById(R.id.login_btn)

       login_btn.setOnClickListener({
           val username = username_input.text.toString()
           val password = password_input.text.toString()
           val request = Request.Builder().url("http://localhost:8080/login").post()
           Log.i("Test","username : $username and Password: $password")


       })
    }

    private fun sendLoginRequest(username: String, password: String) {
        val client = OkHttpClient()

        // Define JSON content type
        val JSON = "application/json; charset=utf-8".

        // Create the JSON body
        val jsonBody = """
            {
                "username": "$username",
                "password": "$password"
            }
        """.trimIndent()

        // Create the request body
        val requestBody = RequestBody.create(JSON, jsonBody)

        // Build the request
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/login") // For emulator: localhost -> 10.0.2.2
            .post(requestBody)
            .build()

        // Run the network call on a background thread
        Thread {
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.i("Response", "Success: $responseBody")
                } else {
                    Log.e("Response", "Failure: ${response.code}")
                }
            } catch (e: IOException) {
                Log.e("Error", "Exception occurred: ${e.message}")
            }
        }.start()
    }
}