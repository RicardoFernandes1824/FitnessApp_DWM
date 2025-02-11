package com.example.fitnessapp.workout_routine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R

class WorkoutRoutineAdapter(private val onClick: (WorkoutRoutine) -> Unit) :
    ListAdapter<WorkoutRoutine, WorkoutRoutineAdapter.WorkoutRoutineViewHolder>(
        WorkoutRoutineDiffCallBack
    ) {

    class WorkoutRoutineViewHolder(itemView: View, val onClick: (WorkoutRoutine) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val workoutRoutineTextView: TextView = itemView.findViewById(R.id.workoutName)
        private val workoutRoutineImageView: ImageView = itemView.findViewById(R.id.workoutIMG)
        private var currentWorkoutRoutine: WorkoutRoutine? = null

        init {
            itemView.setOnClickListener {
                currentWorkoutRoutine?.let { workout -> onClick(workout) }
            }
        }

        fun bind(workout: WorkoutRoutine) {
            currentWorkoutRoutine = workout
            workoutRoutineTextView.text = workout.name

            // Safely handle the image, and provide a default image if null
            val imageResId = workout.image ?: R.drawable.keanu_reeves
            workoutRoutineImageView.setImageResource(imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutRoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.workout_routine, parent, false)
        return WorkoutRoutineViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: WorkoutRoutineViewHolder, position: Int) {
        val workout = getItem(position)
        holder.bind(workout)
    }
}

// DiffUtil for efficient comparison and updates
object WorkoutRoutineDiffCallBack : DiffUtil.ItemCallback<WorkoutRoutine>() {
    override fun areItemsTheSame(oldItem: WorkoutRoutine, newItem: WorkoutRoutine): Boolean {
        return oldItem.id == newItem.id // Compare IDs instead of whole objects
    }

    override fun areContentsTheSame(oldItem: WorkoutRoutine, newItem: WorkoutRoutine): Boolean {
        return oldItem == newItem // Compare full content here
    }
}
