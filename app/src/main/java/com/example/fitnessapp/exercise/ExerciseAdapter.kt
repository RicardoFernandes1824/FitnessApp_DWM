package com.example.fitnessapp.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessapp.R

class ExerciseAdapter(private val onClick: (Exercise) -> Unit) :
    ListAdapter<Exercise, ExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallBack) {

    private val selectedExercises = mutableSetOf<Int>() // Track selected exercises

    class ExerciseViewHolder(itemView: View, val onClick: (Exercise) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val exerciseTextView: TextView = itemView.findViewById(R.id.exerciseName)
        private val exerciseImageView: ImageView = itemView.findViewById(R.id.exerciseIMG)
        private val cardView: androidx.cardview.widget.CardView = itemView.findViewById(R.id.exerciseCard)
        private var currentExercise: Exercise? = null

        fun bind(exercise: Exercise, isSelected: Boolean) {
            currentExercise = exercise
            exerciseTextView.text = exercise.name

            // This updates the background to match selection state
            cardView.isSelected = isSelected

            // Load image with Glide
            Glide.with(itemView.context)
                .load(exercise.image ?: R.drawable.keanu_reeves)
                .placeholder(R.drawable.keanu_reeves)
                .error(R.drawable.keanu_reeves)
                .into(exerciseImageView)

            // Handle click to toggle selection
            itemView.setOnClickListener {
                currentExercise?.let { onClick(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise, parent, false)
        return ExerciseViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = getItem(position)
        val isSelected = selectedExercises.contains(exercise.id)

        holder.bind(exercise, isSelected)

        holder.itemView.setOnClickListener {
            if (isSelected) {
                selectedExercises.remove(exercise.id)
            } else {
                selectedExercises.add(exercise.id)
            }
            notifyItemChanged(position) // Refresh only this item
        }
    }

}

// DiffUtil for efficient comparison and updates
object ExerciseDiffCallBack : DiffUtil.ItemCallback<Exercise>() {
    override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
        return oldItem.id == newItem.id // Compare IDs instead of whole objects
    }

    override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
        return oldItem == newItem // Compare full content here
    }
}
