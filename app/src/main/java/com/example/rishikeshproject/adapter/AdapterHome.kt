package com.example.rishikeshproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.rishikeshproject.ChatActivity
import com.example.rishikeshproject.R
import com.example.rishikeshproject.base.UserModel
import com.example.rishikeshproject.databinding.ItemHomeListBinding

class AdapterHome(var context: Context, private val list: ArrayList<UserModel>,var onclick:ClickListener) :
    RecyclerView.Adapter<AdapterHome.NotificationsHolder>() {

    inner class NotificationsHolder(private val binding: ItemHomeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.apply {
                binding.ivProfile.load(list[pos].image) {
                    placeholder(R.drawable.place_holder)
                }
                binding.tvName.text = list[pos].name
                binding.tvDOB.text = list[pos].email
                binding.tvDOB.text = list[pos].dob

                binding.listItem.setOnClickListener {
                    onclick.click(pos)

                }

            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsHolder {
        return NotificationsHolder(
            ItemHomeListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationsHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

interface ClickListener{
    fun click(pos:Int)
}