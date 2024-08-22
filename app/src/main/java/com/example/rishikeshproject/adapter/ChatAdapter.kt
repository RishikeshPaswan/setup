package com.example.rishikeshproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rishikeshproject.databinding.ChatLayBinding
import com.example.rishikeshproject.model.ChatModel

class ChatAdapter(var list: ArrayList<ChatModel>,var userName: String) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChatLayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }
    fun loadData(listNew: ArrayList<ChatModel>) {
        list= listNew
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list[position].name == userName) {
            holder.binding.layOtherUserMsg.visibility = View.GONE
            holder.binding.layMyMsg.visibility = View.VISIBLE
            holder.binding.txtMyMessage.text = list[position].message
            Log.d("ajdjkAHD", "onBindViewHolder111: "+list[position].name)
            Log.d("ajdjkAHD", "onBindViewHolder2222: "+list[position].message)
        } else {
            holder.binding.layOtherUserMsg.visibility = View.VISIBLE
            holder.binding.layMyMsg.visibility = View.GONE
            holder.binding.txtMessageOtherUser.text = list[position].message
            Log.d("ajdjkAHD", "onBindViewHolder333: "+list[position].name)
            Log.d("ajdjkAHD", "onBindViewHolder4444: "+list[position].message)
        }

    }

    override fun getItemCount(): Int {
        return if (list.isNotEmpty()) list.size else 0
    }

    class ViewHolder(var binding: ChatLayBinding) : RecyclerView.ViewHolder(binding.root)

}