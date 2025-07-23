package com.example.fitnessapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import android.widget.ImageView
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import android.widget.ImageButton
import android.util.Log
import android.webkit.WebView

class ExerciseGuideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exercise_guide_screen)

        val tabLayout = findViewById<TabLayout>(R.id.guideTabLayout)
        tabLayout.addTab(tabLayout.newTab().setText("Description"))
        tabLayout.addTab(tabLayout.newTab().setText("Tips"))

        val exerciseNameText = findViewById<TextView>(R.id.exercise_name)
        val tabContentText = findViewById<TextView>(R.id.tabContent)
        val exerciseVideoWebView = findViewById<WebView>(R.id.exerciseVideo)
        exerciseVideoWebView.settings.javaScriptEnabled = true
        exerciseVideoWebView.settings.loadWithOverviewMode = true
        exerciseVideoWebView.settings.useWideViewPort = true

        val exerciseId = intent.getIntExtra("EXERCISE_ID", -1)
        Log.d("ExerciseGuide", "Received EXERCISE_ID: $exerciseId")
        if (exerciseId != -1) {
            fetchExerciseDetails(exerciseId) { name, description, tips, video ->
                runOnUiThread {
                    Log.d("ExerciseGuide", "Fetched exercise: name=$name, description=$description, tips=$tips, video=$video")
                    exerciseNameText.text = name
                    // Default to description
                    tabContentText.text = description
                    // Load video embed code
                    val html = """
                        <html>
                        <body style=\"margin:0;padding:0;\">
                        $video
                        </body>
                        </html>
                    """.trimIndent()
                    exerciseVideoWebView.loadData(html, "text/html", "utf-8")
                    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab) {
                            tabContentText.text = if (tab.position == 0) description else tips
                        }
                        override fun onTabUnselected(tab: TabLayout.Tab) {}
                        override fun onTabReselected(tab: TabLayout.Tab) {}
                    })
                }
            }
        }

        findViewById<ImageButton>(R.id.goBackEdit).setOnClickListener { finish() }
    }

    private fun fetchExerciseDetails(
        exerciseId: Int,
        callback: (name: String, description: String, tips: String, video: String) -> Unit
    ) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/exercise/$exerciseId"
        Log.d("ExerciseGuide", "Fetching exercise details from: $url")
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("ExerciseGuide", "Failed to fetch exercise: ${e.message}")
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()
                Log.d("ExerciseGuide", "Backend response: $body")
                if (response.isSuccessful && body != null) {
                    val json = JSONObject(body)
                    val name = json.optString("name", "")
                    val description = json.optString("description", "")
                    val tips = json.optString("tips", "")
                    val video = json.optString("video", "")
                    callback(name, description, tips, video)
                }
            }
        })
    }
} 