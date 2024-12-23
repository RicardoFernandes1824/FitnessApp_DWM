package com.example.fitnessapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        // Retrieve the username from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User") // Default to "User" if not found

        // Find the TextView and update it with the username
        val helloText: TextView = findViewById(R.id.helloTXT)
        helloText.text = "Welcome $username"
        Log.i("User", "USER: ${username}")
    }
}
