package com.example.fitnessapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import android.widget.ImageView
import androidx.core.content.ContextCompat
import android.util.Log

class SessionAdapter(
    private val sessions: List<SessionCard>,
    private val onSessionClick: (SessionCard) -> Unit
) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session_card, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.username.text = session.user ?: "-"
        holder.timeAgo.text = getTimeAgo(session.endTime)
        holder.workoutName.text = session.workoutName
        holder.time.text = getDuration(session.startTime, session.endTime)
        holder.volume.text = getVolumeString(session)
        holder.card.setOnClickListener { onSessionClick(session) }

        // Exercises
        holder.exercisesLayout.removeAllViews()
        val exercisesToShow = session.exercises.take(3)
        for (exercise in exercisesToShow) {
            val exView = LayoutInflater.from(holder.itemView.context)
                .inflate(android.R.layout.simple_list_item_1, holder.exercisesLayout, false) as TextView
            exView.text = "${exercise.sets} sets ${exercise.name}${exercise.type?.let { " ($it)" } ?: ""}"
            exView.textSize = 15f
            exView.setTextColor(holder.itemView.context.getColor(android.R.color.black))
            holder.exercisesLayout.addView(exView)
        }
        if (session.exercises.size > 3) {
            holder.seeMore.visibility = View.VISIBLE
            holder.seeMore.text = "See ${session.exercises.size - 3} more exercises"
        } else {
            holder.seeMore.visibility = View.GONE
        }

        // Like icon toggle
        var isLiked = false
        holder.likeIcon.setOnClickListener {
            isLiked = !isLiked
            val color = if (isLiked) R.color.blue else R.color.darkGrey
            holder.likeIcon.setColorFilter(ContextCompat.getColor(holder.itemView.context, color), android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    override fun getItemCount() = sessions.size

    class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.sessionCard)
        val username: TextView = view.findViewById(R.id.sessionUsername)
        val timeAgo: TextView = view.findViewById(R.id.sessionTimeAgo)
        val workoutName: TextView = view.findViewById(R.id.sessionWorkoutName)
        val time: TextView = view.findViewById(R.id.sessionTime)
        val volume: TextView = view.findViewById(R.id.sessionVolume)
        val exercisesLayout: LinearLayout = view.findViewById(R.id.sessionExercisesLayout)
        val seeMore: TextView = view.findViewById(R.id.sessionSeeMore)
        val likeIcon: ImageView = view.findViewById(R.id.sessionLikeIcon)
        val commentIcon: ImageView = view.findViewById(R.id.sessionCommentIcon)
    }

    private fun getTimeAgo(endTime: String?): String {
        if (endTime == null) return "-"
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")
            val endDate = isoFormat.parse(endTime) ?: return "-"
            val now = Calendar.getInstance()
            val endCal = Calendar.getInstance().apply { time = endDate }
            val diffMillis = now.timeInMillis - endCal.timeInMillis

            // Check if it was yesterday
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DATE, -1)
            }
            val isYesterday = yesterday.get(Calendar.YEAR) == endCal.get(Calendar.YEAR) &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR)

            return when {
                diffMillis < 60 * 1000 -> "Just now"
                diffMillis < 60 * 60 * 1000 -> "${diffMillis / (60 * 1000)}m ago"
                diffMillis < 24 * 60 * 60 * 1000 && now.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR) -> "${diffMillis / (60 * 60 * 1000)}h ago"
                isYesterday -> "Yesterday"
                else -> {
                    val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                    sdf.format(endDate)
                }
            }
        } catch (e: Exception) {
            "-"
        }
    }

    private fun getDuration(start: String?, end: String?): String {
        if (start == null || end == null) return "-"
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val startDate = isoFormat.parse(start)
            val endDate = isoFormat.parse(end)
            val diff = (endDate!!.time - startDate!!.time) / 1000 // seconds
            val min = diff / 60
            if (min > 0) "$min min" else "<1 min"
        } catch (e: Exception) {
            "-"
        }
    }

    private fun getVolumeString(session: SessionCard): String {
        // Volume = sum of (weight * reps) for all sets
        var total = 0
        for (set in session.allSets) {
            total += (set.weight ?: 0) * (set.reps ?: 0)
        }
        return if (total > 0) String.format("%,d kg", total) else "-"
    }
}

// Update your SessionCard and ExerciseDone data classes to include username, exercises, sets, reps, weight, and type as needed. 