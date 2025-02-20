package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class CreateWorkout : AppCompatActivity() {

    private lateinit var goBackButton: ImageButton
    private lateinit var addExerciseBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_workout_screen)

        goBackButton = findViewById(R.id.goBackCreateWorkout)
        addExerciseBtn = findViewById(R.id.addExercise)

        goBackButton.setOnClickListener{
            finish()
        }

        addExerciseBtn.setOnClickListener{
            val intent = Intent(this@CreateWorkout, AddExercises::class.java)
            startActivity(intent)
        }
    }
}