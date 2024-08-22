package com.example.rishikeshproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rishikeshproject.Prefs.Companion.preferences
import com.example.rishikeshproject.adapter.ChatAdapter
import com.example.rishikeshproject.base.BaseActivity
import com.example.rishikeshproject.databinding.ActivityChatBinding
import com.example.rishikeshproject.model.ChatModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private var databaseReference: DatabaseReference? = null

    private lateinit var chatWithFirebaseAdapter: ChatAdapter
    private var listChat: ArrayList<ChatModel> = ArrayList()


    private var userName = ""
    private var otherUserName = ""
    private var otherUserid = ""
    private var myuser_id = ""
    private var room_id = ""
    private lateinit var database: DatabaseReference
    lateinit var listener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)



        preferences = getSharedPreferences("Login_Database", Context.MODE_PRIVATE)
        otherUserName = intent.getStringExtra("name").toString()
        otherUserid = intent.getStringExtra("id").toString()
        room_id = intent.getStringExtra("room_id").toString()
        userName = preferences.getString("name", "").toString()
        myuser_id = preferences.getString("id", "").toString()
        chatWithFirebaseAdapter = ChatAdapter(listChat, userName)
        binding.recyclerChat.adapter = chatWithFirebaseAdapter

        getRoomId()
        getChat()


        binding.imgSend.setOnClickListener {
            if (binding.edtxMessage.text.toString().trim().isNotEmpty()) {
                val model = ChatModel()
                model.name = userName
                model.message = binding.edtxMessage.text.toString().trim()
                databaseReference!!.child(room_id).child(getKey()).setValue(model)
                    .addOnCompleteListener {
                        binding.edtxMessage.setText("")
                        UIUtil.hideKeyboard(this)
                        if (it.isSuccessful) {
                            Log.d("TAG", "onCreate: message sent!")
                        } else {

                            Toast.makeText(
                                this,
                                "Something went wrong please try again later!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    }

            }

        }
        setEventListener(this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {

            }
        })
    }


    private fun getRoomId() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatNodeDemo")
        listener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        if (item.key.toString() == myuser_id + otherUserid) {
                            room_id = myuser_id + otherUserid
                            getChat()
                        } else if (item.key.toString() == otherUserid + myuser_id) {
                            room_id = otherUserid + myuser_id
                            getChat()
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    override fun onResume() {
        super.onResume()
        getChat()

    }

    private fun getKey(): String {
        return databaseReference!!.push().key.toString()
    }

    private fun getChat() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatNodeDemo")
        databaseReference!!.child(room_id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listChat.clear()
                if (dataSnapshot.exists()) {
                    for (item in dataSnapshot.children) {
                        val user: ChatModel = item.getValue(ChatModel::class.java)!!
                        listChat.add(user)

                    }

                    chatWithFirebaseAdapter.loadData(listChat)
                    binding.recyclerChat.scrollToPosition(listChat.size - 1)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Fail to get data $error", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }


}