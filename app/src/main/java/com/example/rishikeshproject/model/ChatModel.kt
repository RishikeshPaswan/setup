package com.example.rishikeshproject.model

import java.io.Serializable

data class ChatModel(

    var name: String? = null,
    var message: String? = null

) : Serializable
