package com.example.fitnessapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fitnessapp.databinding.HomePageBinding

class HomePage : AppCompatActivity() {

    private lateinit var binding: HomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedTab = intent.getStringExtra("SELECTED_TAB")
        if (selectedTab == "workout") {
            binding.bottomNavigationView.selectedItemId = R.id.workout
            replaceFragment(Workout())
        } else {
            binding.bottomNavigationView.selectedItemId = R.id.workout
            replaceFragment(Workout())
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.workout -> replaceFragment(Workout())
                R.id.perfil -> replaceFragment(Profile())
                R.id.settings -> replaceFragment(Settings())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment).commit()
    }
}