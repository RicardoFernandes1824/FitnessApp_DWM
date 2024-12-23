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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class Login : AppCompatActivity() {

    lateinit var username_input : EditText
    lateinit var password_input : EditText
    lateinit var login_btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
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
            postLoginRequestOkHttp(username, password)
            Log.i("Test","username : $username and Password: $password")


        })
    }

    fun postLoginRequestOkHttp(username: String, password: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/login"

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
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println("Login successful: ${response.body?.string()}")
                } else {
                    println("Login failed: ${response.code}")
                }
            }
        })
    }
}