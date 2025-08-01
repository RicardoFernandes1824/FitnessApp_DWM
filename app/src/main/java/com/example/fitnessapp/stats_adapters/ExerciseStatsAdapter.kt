package com.example.fitnessapp.stats_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.exercise.Exercise
import android.widget.ImageView
import com.bumptech.glide.Glide

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
            val imageURL = exercise.imageURL
            if (!imageURL.isNullOrEmpty()) {
                Glide.with(exerciseImage.context)
                    .load("http://10.0.2.2:8080$imageURL")
                    .placeholder(R.drawable.icon_chest)
                    .into(exerciseImage)
            } else {
                exerciseImage.setImageResource(R.drawable.icon_chest)
            }
        }
    }
} 