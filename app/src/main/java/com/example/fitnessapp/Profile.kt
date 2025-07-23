
    package com.example.fitnessapp

    import android.content.Context
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import okhttp3.*
    import org.json.JSONArray
    import org.json.JSONObject
    import java.io.IOException
    import java.text.DateFormat
    import java.text.ParseException
    import java.text.SimpleDateFormat
    import java.util.*
    import android.content.Intent
    import android.widget.LinearLayout
    import android.view.Gravity
    import android.text.Editable
    import android.text.TextWatcher
    import android.widget.EditText
    import java.util.Locale

    class Profile : Fragment() {
        private lateinit var sessionRecyclerView: RecyclerView
        private lateinit var sessionAdapter: SessionAdapter
        private val sessions = mutableListOf<SessionCard>()
        private var user: String? = null
        private lateinit var searchEditText: EditText
        private var allSessions: List<SessionCard> = emptyList()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_profile, container, false)

            // Retrieve the username from SharedPreferences
            val sharedPreferences =
                requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            user = sharedPreferences.getString("username", "User")
            val userIdString = sharedPreferences.getString("userId", null)
            val userId = userIdString?.toIntOrNull() ?: -1

            // Fetch and set profile image (reused from Settings)
            val profileImageView = view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileImageView)
            val token = sharedPreferences.getString("token", "") ?: ""
            if (!userIdString.isNullOrEmpty() && token.isNotEmpty()) {
                val client = okhttp3.OkHttpClient()
                val url = "http://10.0.2.2:8080/users/$userIdString"
                val request = okhttp3.Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                        // Optionally handle error
                    }
                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        response.use {
                            if (!response.isSuccessful) return
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                val jsonObject = org.json.JSONObject(responseBody)
                                val photo = jsonObject.optString("photo", null)
                                if (!photo.isNullOrEmpty()) {
                                    requireActivity().runOnUiThread {
                                        com.bumptech.glide.Glide.with(this@Profile)
                                            .load("http://10.0.2.2:8080$photo")
                                            .into(profileImageView)
                                    }
                                }
                            }
                        }
                    }
                })
            }

            // Update the TextView with the username
            val helloText: TextView = view.findViewById(R.id.helloTxt)
            helloText.text = "Welcome, $user"
            Log.i("User", "USER: $user")

            val calendar = Calendar.getInstance().time
            val dateFormat = DateFormat.getDateInstance().format(calendar)
            val dateTextView: TextView = view.findViewById(R.id.dateTxt)
            dateTextView.text = dateFormat

            // Setup RecyclerView
            sessionRecyclerView = view.findViewById(R.id.sessionRecyclerView)
            sessionAdapter = SessionAdapter(sessions) { session ->
                // Navigate to session detail view, passing session.id
                val intent = android.content.Intent(requireContext(), SessionDetailActivity::class.java)
                intent.putExtra("SESSION_ID", session.id)
                startActivity(intent)
            }
            sessionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            sessionRecyclerView.adapter = sessionAdapter

            searchEditText = view.findViewById(R.id.editTextText)
            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { filterSessions() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            if (userId != -1) {
                fetchSessions(userId)
            }

            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val sessionsStatsBtn: Button = view.findViewById(R.id.sessionsStatsBtn)
            val exerciseStatsBtn: Button = view.findViewById(R.id.exerciseStatsBtn)

            sessionsStatsBtn.setOnClickListener {
                val intent = Intent(requireContext(), StatsActivity::class.java)
                intent.putExtra("STATS_TYPE", "sessions")
                startActivity(intent)
            }

            exerciseStatsBtn.setOnClickListener {
                val intent = Intent(requireContext(), StatsActivity::class.java)
                intent.putExtra("STATS_TYPE", "exercises")
                startActivity(intent)
            }
        }

        private fun fetchSessions(userId: Int) {
            val url = "http://10.0.2.2:8080/workoutSession/user/$userId"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Profile", "Failed to fetch sessions", e)
                }
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    if (response.isSuccessful && body != null) {
                        val jsonArr = JSONArray(body)
                        val sessionList = mutableListOf<SessionCard>()
                        for (i in 0 until jsonArr.length()) {
                            val obj = jsonArr.getJSONObject(i)
                            if (obj.optBoolean("finished", false)) {
                                val id = obj.getInt("id")
                                val workoutName = obj.optJSONObject("workout")?.optString("name") ?: "-"
                                val startTime = obj.optString("startTime", null)
                                val endTime = obj.optString("endTime", null)
                                // Parse exercises from trainningSessionSet
                                val setsArr = obj.optJSONArray("trainningSessionSet") ?: JSONArray()
                                val allSetsList = mutableListOf<SessionSetRow>()
                                for (j in 0 until setsArr.length()) {
                                    val setObj = setsArr.getJSONObject(j)
                                    val exName = setObj.optJSONObject("exercise")?.optString("name") ?: "-"
                                    val reps = setObj.optString("reps", "0").toIntOrNull() ?: 0
                                    val weight = setObj.optString("weight", "0").toIntOrNull() ?: 0
                                    val setNumber = setObj.optInt("setNumber", 0)
                                    Log.d("Profile", "Parsed set: exName=$exName, reps=$reps, weight=$weight")
                                    allSetsList.add(SessionSetRow(setNumber, exName, weight, reps))
                                }
                                // Group sets by exercise name and count
                                val setsByExercise = allSetsList.groupBy { it.exerciseName }
                                val exercises = setsByExercise.map { (exName, sets) ->
                                    ExerciseDone(exName, sets.size, sets.lastOrNull()?.reps, sets.lastOrNull()?.weight, null)
                                }
                                Log.d("MY_CENAS", exercises.toString())
                                sessionList.add(SessionCard(id, workoutName, startTime, endTime, user, exercises, allSetsList))
                            }
                        }
                        requireActivity().runOnUiThread {
                            allSessions = sessionList
                            sessions.clear()
                            sessions.addAll(sessionList)
                            sessionAdapter.notifyDataSetChanged()
                        }
                    }
                }
            })
        }

        private fun formatDate(isoString: String?): String {
            if (isoString.isNullOrEmpty()) return ""
            return try {
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = isoFormat.parse(isoString)
                val desiredFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                if (date != null) desiredFormat.format(date) else ""
            } catch (e: ParseException) {
                ""
            }
        }

        private fun filterSessions() {
            val query = searchEditText.text.toString().trim().lowercase()
            val filtered = allSessions.filter { session ->
                query.isEmpty() ||
                session.workoutName.lowercase().contains(query) ||
                formatDate(session.endTime).lowercase().contains(query)
            }
            sessions.clear()
            sessions.addAll(filtered)
            sessionAdapter.notifyDataSetChanged()
        }
    }

    data class SessionCard(
        val id: Int,
        val workoutName: String,
        val startTime: String?,
        val endTime: String?,
        val user: String?,
        val exercises: List<ExerciseDone>,
        val allSets: List<SessionSetRow>
    )

    data class ExerciseDone(
        val name: String,
        val sets: Int,
        val reps: Int?,
        val weight: Int?,
        val type: String? = null
    )

    data class SessionSetRow(
        val setNumber: Int,
        val exerciseName: String,
        val weight: Int?,
        val reps: Int?
    )
