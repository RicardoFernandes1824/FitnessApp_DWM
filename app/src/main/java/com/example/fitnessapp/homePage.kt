package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomePage : AppCompatActivity() {

    lateinit var chestWorkoutButton: Button
    lateinit var backWorkoutButton: Button
    lateinit var legWorkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        chestWorkoutButton = findViewById(R.id.chestWorkout)
        backWorkoutButton = findViewById(R.id.backWorkout)
        legWorkoutButton = findViewById(R.id.legWorkout)

        // Retrieve the username from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User") // Default to "User" if not found

        // Find the TextView and update it with the username
        val helloText: TextView = findViewById(R.id.helloTXT)
        helloText.text = "Welcome $username"
        Log.i("User", "USER: ${username}")

        chestWorkoutButton.setOnClickListener{
            val intent = Intent(this@HomePage, ChestWorkout::class.java)
            startActivity(intent)
        }
    }

}
