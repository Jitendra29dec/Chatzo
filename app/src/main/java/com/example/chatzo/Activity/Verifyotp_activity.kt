package com.example.chatzo.Activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.chatzo.Activity.Verifyotp_activity
import com.example.chatzo.Models.LoginResponseModel
import com.example.chatzo.Models.User
import com.example.chatzo.Models.VerifyOtpModel
import com.example.chatzo.R
import com.example.chatzo.controllers.RetroFitClient
import com.example.chatzo.controllers.UserSessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Verifyotp_activity constructor() : AppCompatActivity() {
    lateinit var card_otp: CardView
    lateinit var question_mark_icon: ImageView
    lateinit var pbLoading: ProgressBar
    lateinit var tvResend: TextView
    lateinit var etOne: TextInputEditText
    lateinit var etTwo: TextInputEditText
    lateinit var etThree: TextInputEditText
    lateinit var etFour: TextInputEditText
    var sessionManager: UserSessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifyotp_activity)
        sessionManager = UserSessionManager(this@Verifyotp_activity)
        question_mark_icon = findViewById(R.id.question_mark_icon)
        card_otp = findViewById(R.id.card_otp)
        pbLoading = findViewById(R.id.pbLoading)
        tvResend = findViewById(R.id.tvResend)
        etFour = findViewById(R.id.etFour)
        etOne = findViewById(R.id.etOne)
        etTwo = findViewById(R.id.etTwo)
        etThree = findViewById(R.id.etThree)
        question_mark_icon.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val menu: PopupMenu = PopupMenu(this@Verifyotp_activity, v)
                menu.getMenu().add("Help")
                menu.show()
            }
        })
        tvResend.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                pbLoading.setVisibility(View.VISIBLE)

                val android_id: String = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)

                val `object`: JsonObject = JsonObject()
                `object`.addProperty("imei", android_id)
                `object`.addProperty("mobile", getIntent().getStringExtra("mobile"))

                Log.w("SHANTAG", "Object " + `object`)

                val call: Call<LoginResponseModel?>? = RetroFitClient
                        .instance?.api?.resendOtp(`object`)
                call?.enqueue(object : Callback<LoginResponseModel?> {
                    override fun onResponse(call: Call<LoginResponseModel?>, response: Response<LoginResponseModel?>) {
                        try {
                            Log.w("BTAG", "Responnse" + response.body())
                            if ((response.body()!!.success == "1")) {
                                Toast.makeText(this@Verifyotp_activity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@Verifyotp_activity, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                            }
                            pbLoading.setVisibility(View.GONE)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            pbLoading.setVisibility(View.GONE)
                        }
                    }

                    override fun onFailure(call: Call<LoginResponseModel?>, t: Throwable) {
                        pbLoading.setVisibility(View.GONE)
                        Toast.makeText(this@Verifyotp_activity, "fail", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })
        card_otp.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                if ((etOne.getText().toString().trim({ it <= ' ' }) == "")) {
                    Toast.makeText(this@Verifyotp_activity, "Please enter otp", Toast.LENGTH_SHORT).show()
                } else if ((etTwo.getText().toString().trim({ it <= ' ' }) == "")) {
                    Toast.makeText(this@Verifyotp_activity, "Please enter otp", Toast.LENGTH_SHORT).show()
                } else if ((etThree.getText().toString().trim({ it <= ' ' }) == "")) {
                    Toast.makeText(this@Verifyotp_activity, "Please enter otp", Toast.LENGTH_SHORT).show()
                } else if ((etFour.getText().toString().trim({ it <= ' ' }) == "")) {
                    Toast.makeText(this@Verifyotp_activity, "Please enter otp", Toast.LENGTH_SHORT).show()
                } else {
                    pbLoading.setVisibility(View.VISIBLE)
                    val android_id: String = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID)
                    val `object`: JsonObject = JsonObject()
                    `object`.addProperty("imei", android_id)
                    `object`.addProperty("mobile", getIntent().getStringExtra("mobile"))
                    `object`.addProperty("otp", etOne.getText().toString().trim({ it <= ' ' }) + etTwo.getText().toString().trim({ it <= ' ' }) + etThree.getText().toString().trim({ it <= ' ' }) + etFour.getText().toString().trim({ it <= ' ' }))
                    Log.w("SHANTAG", "Object " + `object`)
                    val call: Call<VerifyOtpModel?>? = RetroFitClient
                            .instance?.api?.otpVerification(`object`)
                    call?.enqueue(object : Callback<VerifyOtpModel?> {

                        override fun onResponse(call: Call<VerifyOtpModel?>, response: Response<VerifyOtpModel?>) {
                            try {
                                Log.w("BTAG", "Responnse" + response.body())
                                if ((response.body()!!.success == "1")) {
                                    val user: User = User()
                                    user.setmUserId(response.body()!!.id)
                                    user.pinEnter = ""
                                    sessionManager!!.createUserLoginSession(user)

                                    // Intent intent=new Intent(Verifyotp_activity.this, Profile_info_activity.class);
                                    val intent: Intent = Intent(this@Verifyotp_activity, ActCreatePin::class.java)

                                    intent.putExtra("id", response.body()!!.id)
                                    intent.putExtra("imei", android_id)
                                    intent.putExtra("mobile", getIntent().getStringExtra("mobile"))
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@Verifyotp_activity, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                                }
                                pbLoading.setVisibility(View.GONE)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                pbLoading.setVisibility(View.GONE)
                            }
                        }

                        override fun onFailure(call: Call<VerifyOtpModel?>, t: Throwable) {
                            pbLoading.setVisibility(View.GONE)
                            Toast.makeText(this@Verifyotp_activity, "fail", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        })
        etOne.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            public override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etOne.getText().toString().trim({ it <= ' ' }).length == 1) {
                    etTwo.requestFocus()
                } else if (etOne.getText().toString().trim({ it <= ' ' }).length == 0) {
                }
            }

            public override fun afterTextChanged(editable: Editable) {}
        })
        etTwo.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            public override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etTwo.getText().toString().trim({ it <= ' ' }).length == 1) {
                    etThree.requestFocus()
                } else if (etTwo.getText().toString().trim({ it <= ' ' }).length == 0) {
                    etOne.requestFocus()
                }
            }

            public override fun afterTextChanged(editable: Editable) {}
        })

        etThree.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            public override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etThree.getText().toString().trim({ it <= ' ' }).length == 1) {
                    etFour.requestFocus()
                } else if (etThree.getText().toString().trim({ it <= ' ' }).length == 0) {
                    etTwo.requestFocus()
                }
            }

            public override fun afterTextChanged(editable: Editable) {}
        })

        etFour.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            public override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etFour.getText().toString().trim({ it <= ' ' }).length == 1) {
                } else if (etFour.getText().toString().trim({ it <= ' ' }).length == 0) {
                    etThree.requestFocus()
                }
            }

            public override fun afterTextChanged(editable: Editable) {}
        })
    }


}