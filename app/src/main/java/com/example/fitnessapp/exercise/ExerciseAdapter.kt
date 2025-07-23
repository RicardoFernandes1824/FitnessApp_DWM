package com.example.fitnessapp.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.ImageView
import com.bumptech.glide.Glide

class ExerciseAdapter(
    private val onSelectionChanged: (selectedCount: Int) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private var exercises: List<Exercise> = emptyList()
    private val selectedPositions = mutableSetOf<Int>()

    fun submitList(list: List<Exercise>) {
        exercises = list
        selectedPositions.clear()
        notifyDataSetChanged()
        onSelectionChanged(0)
    }

    fun getSelectedExercises(): List<Exercise> = selectedPositions.map { exercises[it] }

    fun setInitialSelection(selected: List<Exercise>) {
        selectedPositions.clear()
        selected.forEach { sel ->
            exercises.indexOfFirst { it.id == sel.id }.takeIf { it >= 0 }?.let { selectedPositions.add(it) }
        }
        notifyDataSetChanged()
        onSelectionChanged(selectedPositions.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position], position, selectedPositions.contains(position))
    }

    override fun getItemCount(): Int = exercises.size

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.exercise_name)
        private val cardLayout: ConstraintLayout = itemView.findViewById(R.id.exerciseCardLayout)

        fun bind(exercise: Exercise, position: Int, isSelected: Boolean) {
            nameTextView.text = exercise.name
            cardLayout.isSelected = isSelected
            // Fetch and display the image
            val imageView = itemView.findViewById<ImageView>(R.id.exerciseIMG)
            val imageURL = exercise.imageURL
            if (!imageURL.isNullOrEmpty()) {
                Glide.with(imageView.context)
                    .load("http://10.0.2.2:8080$imageURL")
                    .placeholder(R.drawable.icon_chest)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.icon_chest)
            }
            itemView.setOnClickListener {
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove(position)
                } else {
                    selectedPositions.add(position)
                }
                notifyItemChanged(position)
                onSelectionChanged(selectedPositions.size)
            }
        }
    }
} 