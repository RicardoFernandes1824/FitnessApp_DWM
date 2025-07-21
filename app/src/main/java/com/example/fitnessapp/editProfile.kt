package com.example.fitnessapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStream
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.bumptech.glide.Glide

class editProfile : AppCompatActivity() {
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var saveChangesButton: Button
    private lateinit var goBackEditButton: ImageButton
    private lateinit var profileImageView: CircleImageView
    private lateinit var changeImgButton: ImageButton
    private val PICK_IMAGE_REQUEST = 1001
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.edit_profile)

        firstNameInput = findViewById(R.id.firstNameEdit)
        lastNameInput = findViewById(R.id.lastNameEdit)
        heightInput = findViewById(R.id.heightEdit)
        weightInput = findViewById(R.id.weightEdit)
        genderSpinner = findViewById(R.id.spinnerEdit)
        saveChangesButton = findViewById(R.id.saveChangesBtn)
        goBackEditButton = findViewById(R.id.goBackEdit)
        profileImageView = findViewById(R.id.circleImageView)
        changeImgButton = findViewById(R.id.changeImg)

        changeImgButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        val genderOptions = resources.getStringArray(R.array.gender_options)

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                view.setBackgroundResource(R.drawable.light_grey_bg)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                view.setBackgroundResource(R.color.lightGrey)
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        val sharedPreferences =
            getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "").orEmpty()
        val token = sharedPreferences.getString("token", "").orEmpty()
        val username = sharedPreferences.getString("username", "").orEmpty()
        val firstName = sharedPreferences.getString("firstName", "").orEmpty()
        val lastName = sharedPreferences.getString("lastName", "").orEmpty()

        val usernameEditTxt: TextView = findViewById(R.id.usernameEditTxt)
        usernameEditTxt.text = "$username"

        val fullName: TextView = findViewById(R.id.fullName)
        fullName.text = "$firstName $lastName"

        fetchUserData(userId, token)

        goBackEditButton.setOnClickListener {
            finish()
        }

        // Handle sign-up button click
        saveChangesButton.setOnClickListener {
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val heightStr = heightInput.text.toString()
            val weightStr = weightInput.text.toString()
            val gender = genderSpinner.selectedItem.toString()


            try {
                val height = heightStr.toInt()
                val weight = weightStr.toInt()
                patchUpdateUserRequestOkHttp(
                    userId,
                    firstName,
                    lastName,
                    gender,
                    height,
                    weight,
                    token
                )

            } catch (e: NumberFormatException) {
                Log.i("UpdateClient", "Height and Weight must be valid integers.")
                return@setOnClickListener
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri)
            uploadProfileImage(selectedImageUri)
        }
    }

    private fun uploadProfileImage(imageUri: Uri?) {
        if (imageUri == null) return
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "") ?: return
        val contentResolver = applicationContext.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        val file = File(cacheDir, "profile_pic.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        val client = OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("http://10.0.2.2:8080/user/$userId/photo")
            .post(MultipartBody.Builder().setType(MultipartBody.FORM).addPart(body).build())
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ProfileImageUpload", "Upload failed: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val photoPath = org.json.JSONObject(responseBody).optString("photo", null)
                    if (photoPath != null) {
                        runOnUiThread {
                            Glide.with(this@editProfile)
                                .load("http://10.0.2.2:8080$photoPath")
                                .into(profileImageView)
                        }
                    }
                    Log.i("ProfileImageUpload", "Upload successful")
                } else {
                    Log.e("ProfileImageUpload", "Upload failed: ${response.code}")
                }
            }
        })
    }

    private fun patchUpdateUserRequestOkHttp(
        userId: String,
        firstName: String,
        lastName: String,
        gender: String,
        height: Int,
        weight: Int,
        token: String
    ) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/users/$userId"

        // Create JSON request body
        val jsonBody = """
    {
        "firstName": "$firstName",
        "lastName": "$lastName",
        "gender": "$gender",
        "height": "$height",
        "weight": "$weight"
    }
    """
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        // Build the request
        val request = Request.Builder()
            .url(url)
            .method("PATCH", requestBody)
            .addHeader("Authorization", "Bearer $token")
            .build()

        // Send the request asynchronously
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
                            finish()
                        }
                    } else {
                        Log.i("UpdateClient", "Update failed with code:${response.code}")
                    }
                }
            }
        })
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
                        val firstName = jsonObject.optString("firstName", "")
                        val lastName = jsonObject.optString("lastName", "")
                        val height = jsonObject.optInt("height", 0)
                        val weight = jsonObject.optInt("weight", 0)
                        val gender = jsonObject.optString("gender", "")
                        val photo = jsonObject.optString("photo", null)

                        // Update UI on the main thread
                        runOnUiThread {
                            firstNameInput.setText(firstName)
                            lastNameInput.setText(lastName)
                            heightInput.setText(if (height != 0) height.toString() else "")
                            weightInput.setText(if (weight != 0) weight.toString() else "")

                            // Set gender selection
                            val genderIndex = resources.getStringArray(R.array.gender_options).indexOf(gender)
                            if (genderIndex != -1) {
                                genderSpinner.setSelection(genderIndex)
                            }
                            // Load profile photo if available
                            if (!photo.isNullOrEmpty()) {
                                Glide.with(this@editProfile)
                                    .load("http://10.0.2.2:8080$photo")
                                    .into(profileImageView)
                            }
                        }
                    }
                }
            }
        })
    }
}
