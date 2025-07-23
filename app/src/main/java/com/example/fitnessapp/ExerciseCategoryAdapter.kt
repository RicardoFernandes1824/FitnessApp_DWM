package com.example.fitnessapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.exercise.Exercise
import com.bumptech.glide.Glide

sealed class ExerciseCategoryItem {
    data class CategoryHeader(val category: String) : ExerciseCategoryItem()
    data class ExerciseItem(val exercise: Exercise) : ExerciseCategoryItem()
}

class ExerciseCategoryAdapter(
    private val onExerciseClick: (Exercise) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<ExerciseCategoryItem>()

    fun submitList(exercises: List<Exercise>) {
        items.clear()
        val grouped = exercises.groupBy { it.category }
        for ((category, exList) in grouped) {
            items.add(ExerciseCategoryItem.CategoryHeader(category))
            for (ex in exList) {
                items.add(ExerciseCategoryItem.ExerciseItem(ex))
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is ExerciseCategoryItem.CategoryHeader -> 0
        is ExerciseCategoryItem.ExerciseItem -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_header, parent, false)
            CategoryHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_by_category, parent, false)
            ExerciseViewHolder(view, onExerciseClick)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ExerciseCategoryItem.CategoryHeader -> (holder as CategoryHeaderViewHolder).bind(item)
            is ExerciseCategoryItem.ExerciseItem -> (holder as ExerciseViewHolder).bind(item.exercise)
        }
    }

    class CategoryHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerText: TextView = itemView.findViewById(R.id.categoryHeaderText)
        fun bind(item: ExerciseCategoryItem.CategoryHeader) {
            headerText.text = item.category
            headerText.setTextColor(Color.parseColor("#FF9800")) // Orange
        }
    }

    class ExerciseViewHolder(itemView: View, val onExerciseClick: (Exercise) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.exerciseNameText)
        private val circleImageView: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.circleImageView)
        private var currentExercise: Exercise? = null
        init {
            itemView.setOnClickListener {
                currentExercise?.let { onExerciseClick(it) }
            }
        }
        fun bind(exercise: Exercise) {
            currentExercise = exercise
            nameText.text = exercise.name
            // Load image using Glide
            val imageURL = exercise.imageURL
            if (!imageURL.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load("http://10.0.2.2:8080$imageURL")
                    .placeholder(R.drawable.keanu_reeves)
                    .into(circleImageView)
            } else {
                circleImageView.setImageResource(R.drawable.keanu_reeves)
            }
        }
    }
} 