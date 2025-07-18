package com.example.fitnessapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ActiveExerciseAdapter(private val exercises: MutableList<ActiveExercise>) :
    RecyclerView.Adapter<ActiveExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name
        // TODO: Load image from URL if needed, else use default
        holder.setsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.setsRecyclerView.adapter = ActiveSetAdapter(exercise.sets)
        // Handle 3-dots menu
        holder.exerciseMenu.setOnClickListener { v ->
            val popup = PopupMenu(v.context, v)
            popup.menu.add("Remove Exercise")
            popup.setOnMenuItemClickListener { item ->
                if (item.title == "Remove Exercise") {
                    exercises.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, exercises.size)
                    true
                } else {
                    false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount() = exercises.size

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: TextView = view.findViewById(R.id.exerciseName)
        val exerciseImage: ImageView = view.findViewById(R.id.exerciseImage)
        val exerciseMenu: ImageButton = view.findViewById(R.id.exerciseMenu)
        val setsRecyclerView: RecyclerView = view.findViewById(R.id.setsRecyclerView)
    }
} 