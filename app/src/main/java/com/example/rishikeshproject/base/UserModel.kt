package com.example.rishikeshproject.base

import java.io.Serializable

data class UserModel(
    val name: String? = null,
    val email: String? = null,
    val dob: String? = null,
    val image: String? = null,
    val room_id: String? = null,
    val uid: String? = null):Serializable

