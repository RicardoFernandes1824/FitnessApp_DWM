package com.example.fitnessapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import java.io.IOException
import okhttp3.*

class AccountSettings : AppCompatActivity() {

    private lateinit var goBackEditButton: ImageButton
    private lateinit var changeUsernameButton: Button
    private lateinit var changeEmailButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var deleteAccountButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_screen)

        goBackEditButton = findViewById(R.id.goBackEdit)
        changeUsernameButton = findViewById(R.id.changeUsername)
        changeEmailButton = findViewById(R.id.changeEmail)
        changePasswordButton = findViewById(R.id.changePassword)
        deleteAccountButton = findViewById(R.id.deleteAccount)

        goBackEditButton.setOnClickListener {
            finish()
        }

        changeUsernameButton.setOnClickListener {
            openEditSettingsActivity("Change Username")
        }

        changeEmailButton.setOnClickListener {
            openEditSettingsActivity("Change Email")
        }

        changePasswordButton.setOnClickListener {
            openEditSettingsActivity("Change Password")
        }

        deleteAccountButton.setOnClickListener{
            showDeleteConfirmationDialog()
        }
    }

    private fun openEditSettingsActivity(settingType: String) {
        val intent = Intent(this, editAccountSettings::class.java)
        intent.putExtra("SETTING_TYPE", settingType)
        startActivity(intent)
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure?")
        builder.setMessage("This is a permanent action and cannot be undone. Do you want to proceed with deleting your account?")

        builder.setPositiveButton("Yes") { _, _ ->
            deleteAccount()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteAccount() {
        val client = OkHttpClient()

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        val token = sharedPreferences.getString("token", "")

        val request = Request.Builder()
            .url("http://10.0.2.2:8080/users/$userId")
            .delete()
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@AccountSettings, MainActivity::class.java)
                        startActivity(intent)

                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error: Unable to delete account", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}