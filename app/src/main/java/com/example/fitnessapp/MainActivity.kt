package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    var binding: MainActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.loginbtn.setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    this@MainActivity,
                    Login::class.java
                )
            )
        }
        binding!!.signupbtn.setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    this@MainActivity,
                    SignUp::class.java
                )
            )
        }
    }
}
