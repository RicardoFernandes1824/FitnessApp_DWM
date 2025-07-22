package com.example.fitnessapp.exercise

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R

class ExerciseSetAdapter(
    private val sets: MutableList<ExerciseSet>
) : RecyclerView.Adapter<ExerciseSetAdapter.SetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_set, parent, false)
        return SetViewHolder(view)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.bind(sets[position], position)
    }

    override fun getItemCount(): Int = sets.size

    inner class SetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val setNumber: TextView = itemView.findViewById(R.id.setNumber)
        private val repsInput: EditText = itemView.findViewById(R.id.repsInput)

        fun bind(set: ExerciseSet, position: Int) {
            setNumber.text = "${position + 1}"
            repsInput.setText(set.reps)
            repsInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { set.reps = s.toString() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }
} 