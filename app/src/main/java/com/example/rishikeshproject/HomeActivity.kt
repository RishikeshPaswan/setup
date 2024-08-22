package com.example.rishikeshproject

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import coil.load
import com.example.rishikeshproject.adapter.AdapterHome
import com.example.rishikeshproject.adapter.ClickListener
import com.example.rishikeshproject.auth.LoginActivity
import com.example.rishikeshproject.databinding.ActivityHomeBinding
import com.example.rishikeshproject.base.BaseActivity
import com.example.rishikeshproject.base.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson

class HomeActivity : BaseActivity<ActivityHomeBinding>(), ClickListener {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    val imagePath = "images/"
    private lateinit var database: DatabaseReference
    private var list = ArrayList<UserModel>()
    private var list2 = ArrayList<UserModel>()


    val imageRef = storageRef.child(imagePath)

    override val bindingInflater: (LayoutInflater) -> ActivityHomeBinding
        get() {
            return ActivityHomeBinding::inflate
        }

    override fun setup() {
        binding.progressBar.visibility = View.VISIBLE

        database = FirebaseDatabase.getInstance().getReference("user")
        preferences = getSharedPreferences("Login_Database", Context.MODE_PRIVATE)
        editor = preferences.edit()
        Log.d("fsdsfgdfhdhdddh", "vvvvvvvvvvvvvv")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("fsdsfgdfhdhdddh", "gdgfdfd")
                binding.progressBar.visibility = View.GONE

                list.clear()
                snapshot.children.forEach {
                    val model = it.getValue(UserModel::class.java)
                    if (model != null) {
                        Log.d("fsdsfgdfhdhdddh", "")
                        if (model.uid.toString() != preferences.getString("id", "").toString()) {
                            list.add(model)
                        } else {
                            list2.add(model)
                        }
                    }
                }
                setAdapter()

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        Log.d("jhklfdjhksfdjk", "" + imagePath)
        onClick()

    }


    private fun onClick() {
        binding.tvLogOut.setOnClickListener {
            loginDialog()
        }
    }

    private fun setAdapter() {
        val adapter = AdapterHome(this@HomeActivity, list,this)
        binding.rvHome.adapter = adapter
    }

    override fun click(pos:Int) {
        var room_id=""
        Prefs.preferences = getSharedPreferences("Login_Database", Context.MODE_PRIVATE)
       var myuser_id = Prefs.preferences.getString("id", "").toString()

        var  databaseReference = FirebaseDatabase.getInstance().getReference("ChatNodeDemo")
         databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        if (item.key.toString() == myuser_id + list[pos].uid) {
                            room_id = myuser_id + list[pos].uid
                        } else if (item.key.toString() == list[pos].uid + myuser_id) {
                          //  room_id = myuser_id + list[pos].uid
                            room_id = list[pos].uid + myuser_id
                        } else {
                            room_id = myuser_id + list[pos].uid
                        }
                    }
                }else{
                    room_id = myuser_id + list[pos].uid
                }

                Log.d("ghhhhhhvdd","jbbbbbbbbbbbbbb")
                startActivity(Intent(this@HomeActivity,ChatActivity::class.java).apply {
                    putExtra("id", list[pos].uid)
                    putExtra("name", list[pos].name)
                    putExtra("room_id", room_id)
                })

            }

             override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun loginDialog() {
        var reviewDialog = Dialog(this)
        reviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        reviewDialog.setContentView(R.layout.dialog_logout)
        reviewDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        reviewDialog.setCancelable(true)
        reviewDialog.setCanceledOnTouchOutside(true)
        reviewDialog.window!!.setGravity(Gravity.CENTER)
        reviewDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogDone = reviewDialog.findViewById<TextView>(R.id.btnYes)
        val dialogNo = reviewDialog.findViewById<TextView>(R.id.btnNo)

        dialogDone.setOnClickListener {
            reviewDialog.dismiss()
            FirebaseAuth.getInstance().signOut();
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
        dialogNo.setOnClickListener {
            reviewDialog.dismiss()
        }
        reviewDialog.show()
    }
}