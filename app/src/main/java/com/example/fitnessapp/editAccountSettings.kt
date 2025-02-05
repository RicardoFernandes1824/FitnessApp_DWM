package com.example.fitnessapp

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class editAccountSettings : AppCompatActivity() {

    private lateinit var goBackEditButton: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var etInput: EditText
    private lateinit var btnUpdate: Button

    private var settingType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_account_screen)

        goBackEditButton = findViewById(R.id.goBackAccountEdit)
        tvTitle = findViewById(R.id.tvTitle)
        etInput = findViewById(R.id.etInput)
        btnUpdate = findViewById(R.id.btnUpdate)

        settingType = intent.getStringExtra("SETTING_TYPE")

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "").orEmpty()
        val token = sharedPreferences.getString("token", "").orEmpty()

        goBackEditButton.setOnClickListener {
            finish()
        }

        tvTitle.text = when (settingType) {
            "Change Username" -> "Change Username"
            "Change Email" -> "Change Email"
            "Change Password" -> "Change Password"
            else -> "Edit Account Settings"
        }

        fetchUserData(userId, token)

        btnUpdate.setOnClickListener {
            val newValue = etInput.text.toString()
            when (settingType) {
                "Change Username" -> {
                    if (newValue.isNotEmpty()) {
                        patchUpdateUserRequestOkHttp(userId, "username", newValue, token)
                    }
                }
                "Change Email" -> {
                    if (newValue.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(newValue).matches()) {
                        patchUpdateUserRequestOkHttp(userId, "email", newValue, token)
                    } else {
                        Log.i("UpdateClient", "Invalid email address.")
                    }
                }
                "Change Password" -> {
                    if (newValue.isNotEmpty() && newValue.length >= 6) {
                        patchUpdateUserRequestOkHttp(userId, "password", newValue, token)
                    } else {
                        Log.i("UpdateClient", "Password should be at least 6 characters long.")
                    }
                }
            }
        }
    }

    private fun fetchUserData(userId: String, token: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/users/$userId"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FetchUserData", "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("FetchUserData", "Request failed: ${response.code}")
                        return
                    }

                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)

                        val username = jsonObject.optString("username", "")
                        val email = jsonObject.optString("email", "")

                        runOnUiThread {
                            when (settingType) {
                                "Change Username" -> {
                                    etInput.setText(username)
                                    etInput.inputType = InputType.TYPE_CLASS_TEXT
                                }

                                "Change Email" -> {
                                    etInput.setText(email)
                                    etInput.inputType =
                                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                                }

                                "Change Password" -> {
                                    etInput.setText("")
                                    etInput.inputType =
                                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun patchUpdateUserRequestOkHttp(
        userId: String,
        field: String,
        newValue: String,
        token: String
    ) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/users/$userId"

        val jsonBody = """
        {
            "$field": "$newValue"
        }
        """
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .method("PATCH", requestBody)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.i("UpdateClient", "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Log.i("UpdateClient", "Update successful: $responseBody")

                        runOnUiThread {
                            when (settingType) {
                                "Change Username" -> etInput.setText(newValue)
                                "Change Email" -> etInput.setText(newValue)
                                "Change Password" -> etInput.setText("")
                            }

                            showSuccessDialog()
                        }
                    } else {
                        Log.i("UpdateClient", "Update failed with code:${response.code}")
                    }
                }
            }
        })
    }

    // Function to show the success dialog and finish the activity
    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage("Updated Successfully")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }
}
