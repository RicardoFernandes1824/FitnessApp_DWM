package com.example.fitnessapp.stats_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.SessionCard
import android.widget.ImageView

class SessionsStatsAdapter(
    private var sessions: List<SessionCard>,
    private val onSessionClick: (SessionCard) -> Unit
) : RecyclerView.Adapter<SessionsStatsAdapter.SessionViewHolder>() {

    fun updateList(newList: List<SessionCard>) {
        sessions = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session_card, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.workoutName.text = session.workoutName
        holder.time.text = session.endTime // Format as needed
        // Set workout icon based on workout name
        val iconRes = when (session.workoutName) {
            "Chest Workout" -> R.drawable.icon_chest
            "Legs Workout" -> R.drawable.icon_legs
            "Back Workout" -> R.drawable.icon_back
            else -> R.drawable.icon_chest
        }
        holder.itemView.setOnClickListener { onSessionClick(session) }
    }

    override fun getItemCount() = sessions.size

    class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val workoutName: TextView = view.findViewById(R.id.sessionWorkoutName)
        val time: TextView = view.findViewById(R.id.sessionTimeAgo)
    }
} 