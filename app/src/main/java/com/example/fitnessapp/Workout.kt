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
    // Removed: private lateinit var createWorkoutBtn: Button

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

        lifecycleScope.launch {
            val workoutRoutines = withContext(Dispatchers.IO) {
                workoutRoutineList(requireContext())
            }

            val showHeader = workoutRoutines.size < 5
            adapter = WorkoutRoutineAdapter(
                showHeader,
                onAddClick = {
                    val intent = Intent(requireContext(), CreateWorkout::class.java)
                    startActivity(intent)
                },
                onItemClick = { workoutRoutine ->
                    val intent = Intent(requireContext(), Workout_Name::class.java)
                    intent.putExtra("WORKOUT_ID", workoutRoutine.id)
                    startActivity(intent)
                }
            )
            recyclerView.adapter = adapter
                adapter.submitList(workoutRoutines)
        }

        return view
    }
}
