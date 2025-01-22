package com.example.fitnessapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class Settings : Fragment() {

    private lateinit var editProfileBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Retrieve the username from SharedPreferences
        val sharedPreferences =
            requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")

        // Update the TextView with the username
        val helloText: TextView = view.findViewById(R.id.userName)
        helloText.text = username

        // Find the button and set its click listener
        editProfileBtn = view.findViewById(R.id.editProfile_btn)

        editProfileBtn.setOnClickListener {
            // Navigate to EditProfile activity
            val intent = Intent(requireContext(), editProfile::class.java)
            startActivity(intent)
        }

        return view
    }
}
