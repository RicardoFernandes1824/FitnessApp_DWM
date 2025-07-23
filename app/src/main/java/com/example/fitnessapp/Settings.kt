package com.example.fitnessapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class Settings : Fragment() {

    private lateinit var editProfileBtn: Button
    private lateinit var accountSettingsBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var exerciseBtn: Button

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

        val profileImageView = view.findViewById<CircleImageView>(R.id.profileImageView)
        val userId = sharedPreferences.getString("userId", "") ?: ""
        val token = sharedPreferences.getString("token", "") ?: ""
        if (userId.isNotEmpty() && token.isNotEmpty()) {
            val client = OkHttpClient()
            val url = "http://10.0.2.2:8080/users/$userId"
            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $token")
                .build()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    // Optionally handle error
                }
                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    response.use {
                        if (!response.isSuccessful) return
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            val jsonObject = JSONObject(responseBody)
                            val photo = jsonObject.optString("photo", null)
                            if (!photo.isNullOrEmpty()) {
                                requireActivity().runOnUiThread {
                                    Glide.with(this@Settings)
                                        .load("http://10.0.2.2:8080$photo")
                                        .into(profileImageView)
                                }
                            }
                        }
                    }
                }
            })
        }

        editProfileBtn = view.findViewById(R.id.editProfile_btn)
        accountSettingsBtn = view.findViewById(R.id.account_btn)
        logoutBtn = view.findViewById(R.id.logout)
        exerciseBtn = view.findViewById(R.id.exercise_btn)

        editProfileBtn.setOnClickListener {
            val intent = Intent(requireContext(), editProfile::class.java)
            startActivity(intent)
        }

        accountSettingsBtn.setOnClickListener {
            val intent = Intent(requireContext(), AccountSettings::class.java)
            startActivity(intent)
        }

        logoutBtn.setOnClickListener {

            val editor: SharedPreferences.Editor = sharedPreferences.edit()

            editor.clear()
            editor.apply()

            val i = Intent(requireActivity(), MainActivity::class.java)

            startActivity(i)
            requireActivity().finish()
        }

        exerciseBtn.setOnClickListener {
            val intent = Intent(requireContext(), ExerciseCategoriesActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
