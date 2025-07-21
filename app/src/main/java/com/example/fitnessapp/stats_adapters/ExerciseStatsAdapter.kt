package com.example.fitnessapp.stats_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.exercise.Exercise
import android.widget.ImageView

class ExerciseStatsAdapter(
    private var exercises: List<Exercise>,
    private val onExerciseClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseStatsAdapter.ExerciseViewHolder>() {

    fun updateList(newList: List<Exercise>) {
        exercises = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise)
        holder.itemView.setOnClickListener { onExerciseClick(exercise) }
    }

    override fun getItemCount() = exercises.size

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val exerciseName: TextView = view.findViewById(R.id.exercise_name)
        private val exerciseImage: ImageView = view.findViewById(R.id.exerciseIMG)
        fun bind(exercise: Exercise) {
            exerciseName.text = exercise.name
            // Optionally load image if imageUrl is set, else use default
            // You can use Glide/Picasso if available, else just set default for now
            // Example:
            // if (exercise.imageUrl != null) { Glide.with(itemView).load(exercise.imageUrl).into(exerciseImage) } else { exerciseImage.setImageResource(R.drawable.chest_icon) }
        }
    }
} 