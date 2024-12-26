package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChestWorkout : AppCompatActivity() {

    lateinit var goBackButton: ImageButton
    lateinit var startWorkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chest_workout)

        goBackButton = findViewById(R.id.goBack_btn)
        startWorkoutButton = findViewById(R.id.startWorkout_btn)

        goBackButton.setOnClickListener{
            val intent = Intent(this@ChestWorkout, HomePage::class.java)
            startActivity(intent)
        }}
    }

