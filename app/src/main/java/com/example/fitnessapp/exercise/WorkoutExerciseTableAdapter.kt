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
import com.bumptech.glide.Glide

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
            val imageURL = workoutExercise.exercise.imageURL
            if (!imageURL.isNullOrEmpty()) {
                Glide.with(exerciseImage.context)
                    .load("http://10.0.2.2:8080$imageURL")
                    .placeholder(R.drawable.icon_chest)
                    .into(exerciseImage)
            } else {
                exerciseImage.setImageResource(R.drawable.icon_chest)
            }
            exerciseName.text = workoutExercise.exercise.name

            // Launch ExerciseGuideActivity on exercise name click
            exerciseName.setOnClickListener {
                val context = itemView.context
                val intent = android.content.Intent(context, com.example.fitnessapp.ExerciseGuideActivity::class.java)
                intent.putExtra("EXERCISE_ID", workoutExercise.exercise.id)
                context.startActivity(intent)
            }

            // Remove exercise via dropdown
            removeExerciseBtn.setOnClickListener {
                val popup = PopupMenu(itemView.context, removeExerciseBtn)
                popup.menu.add("Remove Exercise")
                popup.setOnMenuItemClickListener { menuItem ->
                    if (menuItem.title == "Remove Exercise") {
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