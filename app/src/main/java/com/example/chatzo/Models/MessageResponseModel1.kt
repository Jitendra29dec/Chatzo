package com.example.chatzo.Models

import com.google.gson.annotations.SerializedName
import java.util.*

class MessageResponseModel {
    @SerializedName("success")
    var success: String? = null

    @SerializedName("data")
    var data: ArrayList<MessageRsponseDataModel>? = null

    @SerializedName("msg")
    var msg: String? = null
}