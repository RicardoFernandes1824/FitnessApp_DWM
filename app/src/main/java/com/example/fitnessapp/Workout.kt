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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.workout_routine.WorkoutRoutine
import com.example.fitnessapp.workout_routine.WorkoutRoutineAdapter
import com.example.fitnessapp.workout_routine.workoutRoutineList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Workout : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutRoutineAdapter
    private lateinit var createWorkoutBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")

        val helloText: TextView = view.findViewById(R.id.helloTXT)
        helloText.text = "Welcome, $username!"
        Log.i("User", "USER: $username")

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        createWorkoutBtn = view.findViewById(R.id.createWorkoutBtn)

        createWorkoutBtn.setOnClickListener{
            val intent = Intent(requireContext(), CreateWorkout::class.java)
            startActivity(intent)
        }

        adapter = WorkoutRoutineAdapter { workoutRoutine ->
            val intent = Intent(requireContext(), WorkoutRoutine::class.java)
            intent.putExtra("WORKOUT_ID", workoutRoutine.id) // Passing workout ID
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            val workoutRoutines = withContext(Dispatchers.IO) {
                workoutRoutineList(requireContext())
            }

            workoutRoutines.forEach { routine ->
                Log.i("WorkoutRoutine", "Routine: ${routine.name}")
            }

            if (workoutRoutines.isNotEmpty()) {
                helloText.text = "Welcome, $username!"
                adapter.submitList(workoutRoutines)
            } else {
                helloText.text = "Welcome, $username!"
            }
        }

        return view
    }
}
