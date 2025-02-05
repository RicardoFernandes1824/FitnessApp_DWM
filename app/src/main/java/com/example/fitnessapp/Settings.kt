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
    private lateinit var accountSettingsBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val sharedPreferences =
            requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")

        val helloText: TextView = view.findViewById(R.id.userName)
        helloText.text = username

        editProfileBtn = view.findViewById(R.id.editProfile_btn)
        accountSettingsBtn = view.findViewById(R.id.account_btn)

        editProfileBtn.setOnClickListener {
            val intent = Intent(requireContext(), editProfile::class.java)
            startActivity(intent)
        }

        accountSettingsBtn.setOnClickListener {
            val intent = Intent(requireContext(), AccountSettings::class.java)
            startActivity(intent)
        }

        return view
    }
}
