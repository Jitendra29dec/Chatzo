package com.example.chatzo.Models

import com.google.gson.annotations.SerializedName

class usersModel {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("profile_pic")
    var profile_pic: String? = null
}