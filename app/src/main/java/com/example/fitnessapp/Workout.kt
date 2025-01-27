package com.example.fitnessapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class Workout : Fragment() {

    private lateinit var chestWorkoutButton: Button
    private lateinit var backWorkoutButton: Button
    private lateinit var legWorkoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_workout, container, false)

        // Initialize buttons and other views
        chestWorkoutButton = view.findViewById(R.id.chestWorkout)
        backWorkoutButton = view.findViewById(R.id.backWorkout)
        legWorkoutButton = view.findViewById(R.id.legWorkout)

        // Retrieve the username from SharedPreferences
        val sharedPreferences =
            requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User") // Default to "User" if not found

        // Update the TextView with the username
        val helloText: TextView = view.findViewById(R.id.helloTXT)
        helloText.text = "Welcome $username"
        Log.i("User", "USER: $username")

        // Set button click listeners
        chestWorkoutButton.setOnClickListener {
            val intent = Intent(requireContext(), ChestWorkout::class.java)
            startActivity(intent)
        }

        return view
    }
}
