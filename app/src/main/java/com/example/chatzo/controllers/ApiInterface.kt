package com.example.chatzo.controllers

import com.example.chatzo.Models.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("Chat_register/verifyDevice")
    fun verifyDevice(@Body params: JsonObject?): Call<LoginResponseModel?>?

    @POST("Chat_register/login")
    fun login(@Body params: JsonObject?): Call<LoginResponseModel?>?

    @POST("Chat_register/otpVerification")
    fun otpVerification(@Body params: JsonObject?): Call<VerifyOtpModel?>?

    @POST("Chat_register/resendOtp")
    fun resendOtp(@Body params: JsonObject?): Call<LoginResponseModel?>?

    @POST("Chat_register/profileUpdate")
    fun profileUpdate(@Body params: JsonObject?): Call<LoginResponseModel?>?

    @POST("Chat_register/savePin")
    fun savePin(@Body params: JsonObject?): Call<LoginResponseModel?>?

    @POST("Chat_register/pinLogs")
    fun pinLogs(@Body params: JsonObject?): Call<LoginResponseModel?>?

    @POST("Chat_register/getUser")
    fun getUser(@Body params: JsonObject?): Call<UserModel?>?

    @POST("Messeging_api/sendMessage")
    fun sendMessage(@Body params: JsonObject?): Call<LoginResponseModel?>?

    @POST("Messeging_api/receiveMessage")
    fun receiveMessage(@Body params: JsonObject?): Call<MessageResponseModel?>?

    @POST("Messeging_api/getChatUserList")
    fun getChatUserList(@Body params: JsonObject?): Call<ChatUserResponseModel>
}