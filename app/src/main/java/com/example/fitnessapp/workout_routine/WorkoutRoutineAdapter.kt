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
import android.widget.Button

class WorkoutRoutineAdapter(
    private val showHeader: Boolean,
    private val onAddClick: () -> Unit,
    private val onItemClick: (WorkoutRoutine) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    private var workoutList: List<WorkoutRoutine> = emptyList()

    fun submitList(list: List<WorkoutRoutine>) {
        workoutList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = workoutList.size + if (showHeader) 1 else 0

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && showHeader) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.workout_header, parent, false)
            HeaderViewHolder(view, onAddClick)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.workout_routine, parent, false)
            ItemViewHolder(view, onItemClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            // Nothing to bind for header
        } else if (holder is ItemViewHolder) {
            val itemPosition = if (showHeader) position - 1 else position
            holder.bind(workoutList[itemPosition])
        }
    }

    class HeaderViewHolder(view: View, onAddClick: () -> Unit) : RecyclerView.ViewHolder(view) {
        init {
            view.findViewById<Button>(R.id.createWorkoutBtn).setOnClickListener { onAddClick() }
        }
    }

    class ItemViewHolder(view: View, val onItemClick: (WorkoutRoutine) -> Unit) : RecyclerView.ViewHolder(view) {
        private val workoutRoutineTextView: TextView = view.findViewById(R.id.workoutName)
        private val workoutRoutineImageView: ImageView = view.findViewById(R.id.workoutIMG)
        private var currentWorkoutRoutine: WorkoutRoutine? = null

        init {
            itemView.setOnClickListener {
                currentWorkoutRoutine?.let { workout -> onItemClick(workout) }
            }
        }

        fun bind(workout: WorkoutRoutine) {
            currentWorkoutRoutine = workout
            workoutRoutineTextView.text = workout.name
            val imageResId = workout.image ?: R.drawable.keanu_reeves
            workoutRoutineImageView.setImageResource(imageResId)
        }
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
