package com.example.chatzo.controllers

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetroFitClient private constructor() {
    private val retrofit: Retrofit
    val api: ApiInterface
        get() = retrofit.create(ApiInterface::class.java)

    companion object {
        //  private static final String BaseUrl= "http://r1-world.info/pratyakshik/whatsapp-chat/app_api/index.php/";
        private const val BaseUrl = "http://23.100.84.74/app_api/index.php/"
        private var client: RetroFitClient? = null

        @get:Synchronized
        val instance: RetroFitClient?
            get() {
                if (client == null) {
                    client = RetroFitClient()
                }
                return client
            }
    }

    init {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                            .method(original.method(), original.body())
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }.connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build()
        retrofit = Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                .build()
    }
}