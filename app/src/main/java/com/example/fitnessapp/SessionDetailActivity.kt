package com.example.fitnessapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SessionDetailActivity : AppCompatActivity() {
    private lateinit var sessionName: TextView
    private lateinit var setsRecyclerView: RecyclerView
    private lateinit var adapter: SessionSetAdapter
    private val sets = mutableListOf<SessionSetRow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        sessionName = findViewById(R.id.sessionDetailWorkoutName)
        setsRecyclerView = findViewById(R.id.sessionDetailSetsRecyclerView)
        adapter = SessionSetAdapter(sets)
        setsRecyclerView.layoutManager = LinearLayoutManager(this)
        setsRecyclerView.adapter = adapter

        val sessionId = intent.getIntExtra("SESSION_ID", -1)
        val templateId = intent.getIntExtra("TEMPLATE_ID", -1) // Pass this when opening the activity

        val deleteBtn: Button = findViewById(R.id.deleteTemplateBtn)
        deleteBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Workout Template")
                .setMessage("Are you sure you want to delete this workout template? This action cannot be undone.")
                .setPositiveButton("Delete") { dialog, _ ->
                    dialog.dismiss()
                    if (templateId != -1) {
                        deleteWorkoutTemplate(templateId)
                    } else {
                        Toast.makeText(this, "Template ID not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        if (sessionId != -1) {
            fetchSessionDetail(sessionId)
        }
    }

    private fun fetchSessionDetail(sessionId: Int) {
        val url = "http://10.0.2.2:8080/workoutSession/$sessionId"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SessionDetail", "Failed to fetch session", e)
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val obj = JSONObject(body)
                    val workoutName = obj.optJSONObject("workout")?.optString("name") ?: "-"
                    val setsArr = obj.optJSONArray("trainningSessionSet") ?: JSONArray()
                    val setList = mutableListOf<SessionSetRow>()
                    for (i in 0 until setsArr.length()) {
                        val setObj = setsArr.getJSONObject(i)
                        setList.add(
                            SessionSetRow(
                                setNumber = setObj.optInt("setNumber", 0),
                                exerciseName = setObj.optJSONObject("exercise")?.optString("name") ?: "-",
                                weight = setObj.optInt("weight", 0),
                                reps = setObj.optInt("reps", 0)
                            )
                        )
                    }
                    runOnUiThread {
                        sessionName.text = workoutName
                        sets.clear()
                        sets.addAll(setList)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun deleteWorkoutTemplate(templateId: Int) {
        val url = "http://10.0.2.2:8080/workoutTemplate/$templateId"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SessionDetailActivity, "Failed to delete template", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@SessionDetailActivity, "Template deleted", Toast.LENGTH_SHORT).show()
                        finish() // or navigate to dashboard
                    } else {
                        Toast.makeText(this@SessionDetailActivity, "Failed to delete template", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
} 