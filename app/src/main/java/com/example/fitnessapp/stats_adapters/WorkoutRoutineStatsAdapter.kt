package com.example.fitnessapp.stats_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.workout_routine.WorkoutRoutine

class WorkoutRoutineStatsAdapter(
    private var routines: List<WorkoutRoutine>,
    private val onRoutineClick: (WorkoutRoutine) -> Unit
) : RecyclerView.Adapter<WorkoutRoutineStatsAdapter.RoutineViewHolder>() {

    fun updateList(newList: List<WorkoutRoutine>) {
        routines = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.workout_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.bind(routine)
        holder.itemView.setOnClickListener { onRoutineClick(routine) }
    }

    override fun getItemCount() = routines.size

    class RoutineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val routineName: TextView = view.findViewById(R.id.workoutName)
        private val routineImage: ImageView = view.findViewById(R.id.workoutIMG)
        fun bind(routine: WorkoutRoutine) {
            routineName.text = routine.name
            // Set icon based on routine name
            val iconRes = when (routine.name) {
                "Chest Workout" -> R.drawable.icon_chest
                "Legs Workout" -> R.drawable.icon_legs
                "Back Workout" -> R.drawable.icon_back
                else -> R.drawable.icon_chest
            }
            routineImage.setImageResource(iconRes)
        }
    }
} 