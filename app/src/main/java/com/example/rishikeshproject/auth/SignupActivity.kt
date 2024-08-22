package com.example.rishikeshproject.auth

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import coil.load
import com.example.rishikeshproject.HomeActivity
import com.example.rishikeshproject.R
import com.example.rishikeshproject.base.ImagePickerUtility
import com.example.rishikeshproject.base.UserModel
import com.example.rishikeshproject.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class SignupActivity : ImagePickerUtility<ActivitySignupBinding>() {
    private lateinit var auth: FirebaseAuth
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private var image: Uri? = null
    val storage = FirebaseStorage.getInstance()
    private lateinit var database: DatabaseReference
    var storageReference: StorageReference? = storage.reference
    override fun selectedImage(imagePath: String?, code: Int, uri: Uri?) {
        binding.ivProfile.load(imagePath) {
            placeholder(R.drawable.place_holder)
        }
        image = uri

    }

    override val bindingInflater: (LayoutInflater) -> ActivitySignupBinding
        get() {
            return ActivitySignupBinding::inflate
        }

    override fun setup() {
        preferences = getSharedPreferences("Login_Database", Context.MODE_PRIVATE)
        editor = preferences.edit()
        database = Firebase.database.reference
        onClicks()

    }

    private fun onClicks() {
        // Initialising auth object
        auth = Firebase.auth

        binding.ivProfile.setOnClickListener {
            askStorageManagerPermission(this, 0, false)
        }
        binding.editDob.setOnClickListener {
            dateDialog()
        }
        binding.btnSign.setOnClickListener {
            signUpUser()
        }
        binding.tvSignup.setOnClickListener {
            onBackPressed()
        }
    }

    private fun signUpUser() {
        var image = image
        val name = binding.editName.text.toString()
        val email = binding.editEmail.text.toString()
        val dob = binding.editDob.text.toString()
        val pass = binding.editPassword.text.toString()
        val confirmPassword = binding.editPassword.text.toString()

        // This is Validation
        if (image == null) {
            Toast.makeText(this, "Please select Image", Toast.LENGTH_SHORT).show()
            return
        } else
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
                return
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                return
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
                return
            } else if (dob.isEmpty()) {
                Toast.makeText(this, "Please enter DOB", Toast.LENGTH_SHORT).show()
                return
            } else if (pass.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                return
            } else if (confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show()
                return
            }
        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }

        uploadImage()

    }


    fun uploadImage() {
        var image = image
        val name = binding.editName.text.toString()
        val email = binding.editEmail.text.toString()
        val dob = binding.editDob.text.toString()
        val pass = binding.editPassword.text.toString()
        val confirmPassword = binding.editPassword.text.toString()

        val ref = storageReference
            ?.child(
                "images/"
                        + UUID.randomUUID().toString()
            )


        if (image != null) {
            ref?.putFile(image)
                ?.addOnCompleteListener(OnCompleteListener<UploadTask.TaskSnapshot?> { task ->

                    if (task.isSuccessful) {
                        binding.progressBar.visibility = View.VISIBLE
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            val url = uri.toString()
                            auth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(this) {
                                    if (it.isSuccessful) {
                                        binding.progressBar.visibility = View.GONE

                                        var user = auth.currentUser
                                        val map = HashMap<String, String>()
                                        map["name"] = name
                                        map["email"] = email
                                        map["dob"] = dob
                                        map["image"] = url
                                        map["uid"] = user?.uid.toString()

                                        //                                map["room_id"]= database.push().key.toString()
                                        database.child("user").child(user?.uid.toString())
                                            .setValue(map)
                                        Toast.makeText(
                                            this,
                                            "Successfully Singed Up",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this, HomeActivity::class.java))
                                        moveToHome(user?.uid.toString())
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Singed Up Failed!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }
                })?.addOnFailureListener { e -> // Error, Image not uploaded
                Toast
                    .makeText(
                        this@SignupActivity,
                        "Failed " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
                ?.addOnProgressListener { taskSnapshot ->


                    val progress = (100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount)
                }
        }


    }

    private fun dateDialog() {
        val cal: Calendar = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(cal)
            }

        val datePickerDialog = DatePickerDialog(
            this, R.style.TimePickerDialogTheme, dateSetListener,
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )

        cal[cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)] = cal.get(Calendar.DAY_OF_MONTH)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000

        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
    }

    private fun updateDateInView(cal: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.editDob.setText(sdf.format(cal.time))
    }


    fun moveToHome(id: String) {
        database = FirebaseDatabase.getInstance().getReference("user")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(UserModel::class.java)
                    if (model != null) {
                        if (model.uid.toString() == id) {
                            val shared = getSharedPreferences("Login_Database", MODE_PRIVATE)
                            val editorr = shared.edit()
                            editorr.putString("id", id)
                            editorr.putString("email", model?.email)
                            editorr.putString("name", model?.name)
                            editorr.commit()
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}