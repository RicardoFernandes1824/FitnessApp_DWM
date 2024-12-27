
    package com.example.fitnessapp

    import android.content.Context
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import java.text.DateFormat
    import java.util.Calendar

    class Profile : Fragment() {


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_profile, container, false)


            // Retrieve the username from SharedPreferences
            val sharedPreferences =
                requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val username = sharedPreferences.getString("username", "User") // Default to "User" if not found

            // Update the TextView with the username
            val helloText: TextView = view.findViewById(R.id.helloTxt)
            helloText.text = "Welcome, $username"
            Log.i("User", "USER: $username")

            val calendar = Calendar.getInstance().time
            val dateFormat = DateFormat.getDateInstance().format(calendar)

            val dateTextView: TextView = view.findViewById(R.id.dateTxt)
            dateTextView.text = dateFormat

            return view
        }
    }
