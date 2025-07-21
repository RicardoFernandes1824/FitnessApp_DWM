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
import android.widget.Button
import androidx.recyclerview.widget.ItemTouchHelper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

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
        val setAdapter = ActiveSetAdapter(exercise.sets)
        holder.setsRecyclerView.adapter = setAdapter

        // Swipe to delete sets
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                setAdapter.removeSetAt(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val paint = Paint()
                    paint.color = Color.RED
                    val background = RectF(
                        if (dX > 0) itemView.left.toFloat() else itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        if (dX > 0) itemView.left.toFloat() + dX else itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    c.drawRect(background, paint)

                    // Draw 'Delete' text
                    paint.color = Color.WHITE
                    paint.textSize = 48f
                    paint.textAlign = if (dX > 0) Paint.Align.LEFT else Paint.Align.RIGHT
                    val text = "Delete"
                    val textMargin = 32f
                    val textY = itemView.top + itemView.height / 2f + paint.textSize / 2f - 12
                    if (dX > 0) {
                        c.drawText(text, itemView.left + textMargin, textY, paint)
                    } else {
                        c.drawText(text, itemView.right - textMargin, textY, paint)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        itemTouchHelper.attachToRecyclerView(holder.setsRecyclerView)
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
        // Handle Add Set button
        val addSetBtn = holder.itemView.findViewById<Button>(R.id.addSetBtn)
        addSetBtn.setOnClickListener {
            val newSetNumber = exercise.sets.size + 1
            val newSet = ActiveSet(
                setNumber = newSetNumber,
                previousWeight = null,
                previousReps = null,
                weight = 0,
                reps = 0
            )
            exercise.sets.add(newSet)
            setAdapter.notifyItemInserted(exercise.sets.size - 1)
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