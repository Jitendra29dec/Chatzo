package com.example.chatzo.Activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.chatzo.Activity.Phone_number_activity
import com.example.chatzo.Models.LoginResponseModel
import com.example.chatzo.R
import com.example.chatzo.controllers.RetroFitClient
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Phone_number_activity constructor() : AppCompatActivity() {
   lateinit var card_otp: CardView
   lateinit var question_mark_icon: ImageView
   lateinit var tietPNumber: TextInputEditText
   lateinit var pbLoading: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number_activity)
        question_mark_icon = findViewById(R.id.question_mark_icon)
        card_otp = findViewById(R.id.card_otp)
        tietPNumber = findViewById(R.id.tietPNumber)
        pbLoading = findViewById(R.id.pbLoading)
        question_mark_icon.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val menu: PopupMenu = PopupMenu(this@Phone_number_activity, v)
                menu.getMenu().add("Help")
                menu.show()
            }
        })


        // telephonyManager.getDeviceId();
        card_otp.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                if ((tietPNumber.getText().toString().trim({ it <= ' ' }) == "")) {
                    Toast.makeText(this@Phone_number_activity, "Please enter mobile number", Toast.LENGTH_SHORT).show()
                } else if (tietPNumber.getText().toString().trim({ it <= ' ' }).length < 10) {
                    Toast.makeText(this@Phone_number_activity, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
                } else if (tietPNumber.getText().toString().trim({ it <= ' ' }).startsWith("0") || tietPNumber.getText().toString().trim({ it <= ' ' }).startsWith("1") || tietPNumber.getText().toString().trim({ it <= ' ' }).startsWith("2") || tietPNumber.getText().toString().trim({ it <= ' ' }).startsWith("3") || tietPNumber.getText().toString().trim({ it <= ' ' }).startsWith("4") || tietPNumber.getText().toString().trim({ it <= ' ' }).startsWith("5")) {
                    Toast.makeText(this@Phone_number_activity, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
                } else {
                    pbLoading.setVisibility(View.VISIBLE)
                    val android_id: String = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID)
                    val `object`: JsonObject = JsonObject()
                    `object`.addProperty("imei", android_id)
                    `object`.addProperty("mobile", tietPNumber.getText().toString().trim({ it <= ' ' }))
                    Log.w("SHANTAG", "Object " + `object`)
                    val call: Call<LoginResponseModel?>? = RetroFitClient
                            .instance?.api?.login(`object`)
                    call?.enqueue(object : Callback<LoginResponseModel?> {
                        public override fun onResponse(call: Call<LoginResponseModel?>, response: Response<LoginResponseModel?>) {
                            try {
                                Log.w("BTAG", "Responnse" + response.body())
                                if ((response.body()!!.success == "1")) {
                                    val intent: Intent = Intent(this@Phone_number_activity, Verifyotp_activity::class.java)
                                    intent.putExtra("mobile", tietPNumber.getText().toString().trim({ it <= ' ' }))
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@Phone_number_activity, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                pbLoading.setVisibility(View.GONE)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                pbLoading.setVisibility(View.GONE)
                            }
                        }

                        public override fun onFailure(call: Call<LoginResponseModel?>, t: Throwable) {
                            pbLoading.setVisibility(View.GONE)
                            Toast.makeText(this@Phone_number_activity, "fail", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        })
    }
}