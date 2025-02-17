package com.example.fitnessapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    var binding: MainActivityBinding? = null
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Check if "Remember Me" is true and if user credentials are saved
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)
        val username = sharedPreferences.getString("username", null)
        val token = sharedPreferences.getString("token", null)

        if (rememberMe && !username.isNullOrEmpty() && !token.isNullOrEmpty()) {
            startActivity(Intent(this, HomePage::class.java))
            finish()
        }

        // Navigate to login screen
        binding!!.loginbtn.setOnClickListener { v: View? ->
            startActivity(Intent(this@MainActivity, Login::class.java))
        }

        // Navigate to signup screen
        binding!!.signupbtn.setOnClickListener { v: View? ->
            startActivity(Intent(this@MainActivity, SignUp::class.java))
        }
    }
}
