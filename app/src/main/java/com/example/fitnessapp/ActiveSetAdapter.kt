package com.example.fitnessapp

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color

class ActiveSetAdapter(private val sets: MutableList<ActiveSet>) :
    RecyclerView.Adapter<ActiveSetAdapter.SetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_set, parent, false)
        return SetViewHolder(view)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val set = sets[position]
        holder.setNumber.text = (position + 1).toString()
        // Previous value: show as 'weight x reps' if both available, else '-'
        val prevText = if (set.previousWeight != null && set.previousReps != null) {
            "${set.previousWeight} x ${set.previousReps}"
        } else {
            "-"
        }
        holder.previousValue.text = prevText
        holder.weightInput.setText(if (set.weight > 0) set.weight.toString() else "")
        holder.weightInput.hint = if (set.templateWeight != null && set.templateWeight > 0) set.templateWeight.toString() else "-"
        holder.repsInput.setText(if (set.reps > 0) set.reps.toString() else "")
        holder.repsInput.hint = set.templateReps?.toString() ?: "-"
        holder.confirmSetBtn.alpha = if (set.done) 1.0f else 0.5f
        // Set initial color based on state
        if (set.done) {
            holder.confirmSetBtn.setColorFilter(android.graphics.Color.parseColor("#388E3C"))
        } else {
            holder.confirmSetBtn.clearColorFilter()
        }
        holder.confirmSetBtn.setOnClickListener {
            set.done = !set.done
            if (set.done) {
                holder.confirmSetBtn.setColorFilter(android.graphics.Color.parseColor("#388E3C"))
            } else {
                holder.confirmSetBtn.clearColorFilter()
            }
        }
        holder.weightInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                set.weight = s?.toString()?.toIntOrNull() ?: 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        holder.repsInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                set.reps = s?.toString()?.toIntOrNull() ?: 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount() = sets.size

    fun removeSetAt(position: Int) {
        sets.removeAt(position)
        notifyItemRemoved(position)
    }

    class SetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val setNumber: TextView = view.findViewById(R.id.setNumber)
        val previousValue: TextView = view.findViewById(R.id.previousValue)
        val weightInput: EditText = view.findViewById(R.id.weightInput)
        val repsInput: EditText = view.findViewById(R.id.repsInput)
        val confirmSetBtn: ImageButton = view.findViewById(R.id.confirmSetBtn)
    }
} 