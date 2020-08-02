package com.example.chatzo.Models

import com.google.gson.annotations.SerializedName
import java.util.*

class MessageRsponseDataModel {

    @SerializedName("chats")
    var chats: ArrayList<MessagesModel>? = null

    @SerializedName("date")
    var date: String? = null
}