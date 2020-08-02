package com.example.chatzo.Models

import com.google.gson.annotations.SerializedName

class MessagesModel {
    var dateId: String? = null

    var userId: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("sender_id")
    var sender_id: String? = null

    @SerializedName("receiver_id")
    var receiver_id: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("sent_date")
    var sent_date: String? = null

    @SerializedName("sent_time")
    var sent_time: String? = null

    @SerializedName("delivered_time")
    var delivered_time: String? = null

    @SerializedName("delivered_date")
    var delivered_date: String? = null

    @SerializedName("seen_time")
    var seen_time: String? = null

    @SerializedName("seen_date")
    var seen_date: String? = null

    @SerializedName("media_type")
    var media_type: String? = null

    @SerializedName("file_name")
    var file_name: String? = null

    @SerializedName("file_type")
    var file_type: String? = null

    @SerializedName("file_data")
    var file_data: String? = null

    @SerializedName("message_status")
    var message_status: String? = null

    @SerializedName("is_delete")
    var is_delete: String? = null

    @SerializedName("side")
    var side: String? = null

    @SerializedName("unseenCount")
    var unseenCount = 0
}