package com.example.chatzo.Models

import com.google.gson.annotations.SerializedName

class VerifyOtpModel {
    @SerializedName("success")
    var success: String? = null

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("id")
    var id: String? = null
}