package com.example.fitnessapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionSetAdapter(private val sets: List<SessionSetRow>) :
    RecyclerView.Adapter<SessionSetAdapter.SetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session_set, parent, false)
        return SetViewHolder(view)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val set = sets[position]
        holder.setNumber.text = set.setNumber.toString()
        holder.exerciseName.text = set.exerciseName
        holder.weight.text = set.weight.toString()
        holder.reps.text = set.reps.toString()
    }

    override fun getItemCount() = sets.size

    class SetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val setNumber: TextView = view.findViewById(R.id.sessionSetNumber)
        val exerciseName: TextView = view.findViewById(R.id.sessionSetExerciseName)
        val weight: TextView = view.findViewById(R.id.sessionSetWeight)
        val reps: TextView = view.findViewById(R.id.sessionSetReps)
    }
} 