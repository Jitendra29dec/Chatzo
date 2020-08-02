package com.example.chatzo.Models

import com.google.gson.annotations.SerializedName
import java.util.*

class UserModel {
    @SerializedName("success")
    var success: String? = null

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("users")
    var users: ArrayList<usersModel>? = null
}