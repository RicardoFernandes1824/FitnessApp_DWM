package com.example.fitnessapp.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import android.widget.Button
import android.widget.PopupMenu

class WorkoutExerciseTableAdapter(
    private val exercises: MutableList<WorkoutExercise>,
    private val onRemoveExercise: (WorkoutExercise) -> Unit
) : RecyclerView.Adapter<WorkoutExerciseTableAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val exerciseImage: ImageView = itemView.findViewById(R.id.exerciseImage)
        private val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        private val removeExerciseBtn: ImageButton = itemView.findViewById(R.id.removeExerciseBtn)
        private val setsRecyclerView: RecyclerView = itemView.findViewById(R.id.setsRecyclerView)
        private val addSetBtn: Button = itemView.findViewById(R.id.addSetBtn)
        private lateinit var setAdapter: ExerciseSetAdapter

        fun bind(workoutExercise: WorkoutExercise) {
            // Set image if available, else default
            if (workoutExercise.exercise.imageUrl != null) {
                // TODO: Load image from URL if needed
                exerciseImage.setImageResource(R.drawable.chest_icon)
            } else {
                exerciseImage.setImageResource(R.drawable.chest_icon)
            }
            exerciseName.text = workoutExercise.exercise.name

            // Remove exercise via dropdown
            removeExerciseBtn.setOnClickListener {
                val popup = PopupMenu(itemView.context, removeExerciseBtn)
                popup.menu.add("Delete Exercise")
                popup.setOnMenuItemClickListener { menuItem ->
                    if (menuItem.title == "Delete Exercise") {
                        onRemoveExercise(workoutExercise)
                        true
                    } else {
                        false
                    }
                }
                popup.show()
            }

            // Setup sets adapter
            setAdapter = ExerciseSetAdapter(workoutExercise.sets)
            setsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            setsRecyclerView.adapter = setAdapter

            addSetBtn.setOnClickListener {
                val newSetNumber = workoutExercise.sets.size + 1
                workoutExercise.sets.add(ExerciseSet(setNumber = newSetNumber))
                setAdapter.notifyItemInserted(workoutExercise.sets.size - 1)
            }
        }
    }
} 