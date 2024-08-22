package com.example.rishikeshproject.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.rishikeshproject.HomeActivity
import com.example.rishikeshproject.base.BaseActivity
import com.example.rishikeshproject.base.UserModel
import com.example.rishikeshproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    // Creating firebaseAuth object
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var auth: FirebaseAuth
    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding
        get(){
            return ActivityLoginBinding::inflate
        }
    private lateinit var database: DatabaseReference

    override fun setup() {
        auth = FirebaseAuth.getInstance()
        preferences = getSharedPreferences("Login_Database", Context.MODE_PRIVATE)
        editor = preferences.edit()
     onClick()
    }

    private fun onClick() {
    binding.btnSign.setOnClickListener {
        login()
    }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun login() {

        val email = binding.editEmail.text.toString()
        val pass = binding.editPassword.text.toString()
        if (email.isEmpty() || pass.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        // calling signInWithEmailAndPassword(email, pass)
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
              val user =  auth.currentUser
              var id =  user?.uid.toString()
                moveToHome(id)
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
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
                            editorr.putString("id",id)
                            editorr.putString("email",model?.email)
                            editorr.putString("name",model?.name)
                            editorr.commit()
                            startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                            finish()
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}