package com.example.chatzo.Models

import com.google.gson.annotations.SerializedName
import java.util.*

class ChatUserResponseModel {

    @SerializedName("chatsUsers")
    var chatsUsers: ArrayList<ChatUserListModel>? = null

    @SerializedName("success")
    var success: String? = null

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("allUnreadCount")
    var allUnreadCount: String? = null

}