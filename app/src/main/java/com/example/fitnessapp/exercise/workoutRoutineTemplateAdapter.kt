package com.example.fitnessapp.exercise

import android.content.Intent
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

class workoutRoutineTemplateAdapter(private val onClick: (workoutRoutineTemplate) -> Unit) :
    ListAdapter<workoutRoutineTemplate, workoutRoutineTemplateAdapter.workoutRoutineTemplateViewHolder>(workoutRoutineTemplateDiffCallBack) {

    private val selectedWorkoutRoutines = mutableSetOf<Int>() // Track selected workout routines

    class workoutRoutineTemplateViewHolder(itemView: View, val onClick: (workoutRoutineTemplate) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val routineNameTextView: TextView = itemView.findViewById(R.id.exerciseInfoName)
        private val routineImageView: ImageView = itemView.findViewById(R.id.exerciseInfoIMG)
        private val cardView: androidx.cardview.widget.CardView = itemView.findViewById(R.id.exerciseCardInfo)
        private val routineSetsView: TextView = itemView.findViewById(R.id.setsInfo)
        private val routineRepsView: TextView = itemView.findViewById(R.id.repsInfo)
        private var currentRoutine: workoutRoutineTemplate? = null

        fun bind(workoutRoutineTemplate: workoutRoutineTemplate, isSelected: Boolean) {
            currentRoutine = workoutRoutineTemplate
            routineNameTextView.text = workoutRoutineTemplate.name
            routineSetsView.text = "Sets: ${workoutRoutineTemplate.sets}"
            routineRepsView.text = "Reps: ${workoutRoutineTemplate.reps}"

            // This updates the background to match selection state
            cardView.isSelected = isSelected

            // Load image with Glide
            Glide.with(itemView.context)
                .load(workoutRoutineTemplate.image ?: R.drawable.keanu_reeves)
                .placeholder(R.drawable.keanu_reeves)
                .error(R.drawable.keanu_reeves)
                .into(routineImageView)

            // Handle click to navigate to ExerciseGuideActivity
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ExerciseGuideActivity::class.java)
                intent.putExtra("WORKOUT_ROUTINE_ID", workoutRoutineTemplate.id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): workoutRoutineTemplateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise, parent, false)
        return workoutRoutineTemplateViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: workoutRoutineTemplateViewHolder, position: Int) {
        val routine = getItem(position)
        val isSelected = selectedWorkoutRoutines.contains(routine.id)

        holder.bind(routine, isSelected)
    }
}

// DiffUtil for efficient comparison and updates
object workoutRoutineTemplateDiffCallBack : DiffUtil.ItemCallback<workoutRoutineTemplate>() {
    override fun areItemsTheSame(oldItem: workoutRoutineTemplate, newItem: workoutRoutineTemplate): Boolean {
        return oldItem.id == newItem.id // Compare IDs instead of whole objects
    }

    override fun areContentsTheSame(oldItem: workoutRoutineTemplate, newItem: workoutRoutineTemplate): Boolean {
        return oldItem == newItem // Compare full content here
    }
}
