package com.example.fitnessapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException

class ProgressChartActivity : AppCompatActivity() {
    private lateinit var goBackBtn: ImageButton
    private lateinit var titleText: TextView
    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_chart)

        goBackBtn = findViewById(R.id.goBackBtn)
        titleText = findViewById(R.id.progressTitle)
        chart = findViewById(R.id.progressChart)

        goBackBtn.setOnClickListener { finish() }

        val type = intent.getStringExtra("TYPE") // "routine" or "exercise"
        val name = intent.getStringExtra("NAME") ?: "Progress"
        val id = intent.getIntExtra("ID", -1)
        titleText.text = name

        // TODO: Replace with actual userId from SharedPreferences or auth
        val userId = 1

        CoroutineScope(Dispatchers.IO).launch {
            val (entries, labels, yLabel) = fetchProgressData(type, id, userId)
            runOnUiThread {
                val dataSet = LineDataSet(entries, yLabel)
                dataSet.valueTextSize = 12f
                val lineData = LineData(dataSet)
                chart.data = lineData
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                chart.xAxis.granularity = 1f
                chart.xAxis.labelRotationAngle = -45f
                chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                chart.axisLeft.textSize = 14f
                chart.axisLeft.axisMinimum = 0f
                chart.axisRight.isEnabled = false
                chart.description = Description().apply { text = "" }
                chart.invalidate()
            }
        }
    }

    private fun fetchProgressData(type: String?, id: Int, userId: Int): Triple<List<Entry>, List<String>, String> {
        val client = OkHttpClient()
        val url = if (type == "exercise") {
            "http://10.0.2.2:8080/exercise/$id/progress?userId=$userId"
        } else {
            "http://10.0.2.2:8080/workoutRoutine/$id/progress?userId=$userId"
        }
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return Triple(emptyList(), emptyList(), "")
        val jsonArray = JSONArray(body)
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()
        if (type == "exercise") {
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val date = obj.getString("date")
                val weight = obj.getDouble("weight").toFloat()
                entries.add(Entry(i.toFloat(), weight))
                labels.add(date.substring(0, 10))
            }
            return Triple(entries, labels, "Weight")
        } else {
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val date = obj.getString("date")
                val totalVolume = obj.getDouble("totalVolume").toFloat()
                entries.add(Entry(i.toFloat(), totalVolume))
                labels.add(date.substring(0, 10))
            }
            return Triple(entries, labels, "Total Volume")
        }
    }
} 