package com.example.chatzo.Models

import com.google.gson.annotations.SerializedName

class LoginResponseModel {
    @SerializedName("success")
    var success: String? = null

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("count")
    var count = 0
}