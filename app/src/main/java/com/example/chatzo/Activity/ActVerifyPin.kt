package com.example.chatzo.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.chatzo.Activity.ActVerifyPin
import com.example.chatzo.Models.LoginResponseModel
import com.example.chatzo.R
import com.example.chatzo.controllers.RetroFitClient
import com.example.chatzo.controllers.UserSessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ActVerifyPin constructor() : AppCompatActivity() {
    lateinit var etOne: TextInputEditText
    lateinit var etTwo: TextInputEditText
    lateinit var etThree: TextInputEditText
    lateinit var etFour: TextInputEditText
    lateinit var cvVerify: CardView
    var sessionManager: UserSessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_verify_pin)
        sessionManager = UserSessionManager(this@ActVerifyPin)
        cvVerify = findViewById(R.id.cvVerify)
        etOne = findViewById(R.id.etOne)
        etTwo = findViewById(R.id.etTwo)
        etThree = findViewById(R.id.etThree)
        etFour = findViewById(R.id.etFour)
        cvVerify.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                if ((etOne.getText().toString().trim({ it <= ' ' }) == "") || (etTwo.getText().toString().trim({ it <= ' ' }) == "") || (etThree.getText().toString().trim({ it <= ' ' }) == "") || (etFour.getText().toString().trim({ it <= ' ' }) == "")) {
                    Toast.makeText(this@ActVerifyPin, "Please enter pin", Toast.LENGTH_SHORT).show()
                } else {
                    verifyPin()
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
        calculateTimeDifference()
    }

    private fun verifyPin() {
        val `object`: JsonObject = JsonObject()
        `object`.addProperty("user_id", sessionManager!!.userDetails.get("id"))
        `object`.addProperty("pin", etOne!!.getText().toString().trim({ it <= ' ' }) + etTwo!!.getText().toString().trim({ it <= ' ' }) + etThree!!.getText().toString().trim({ it <= ' ' }) + etFour!!.getText().toString().trim({ it <= ' ' }))
        Log.w("SHANTAG", "Object " + `object`)
        val call: Call<LoginResponseModel?>? = RetroFitClient
                .instance?.api?.pinLogs(`object`)
        call?.enqueue(object : Callback<LoginResponseModel?> {
             override fun onResponse(call: Call<LoginResponseModel?>, response: Response<LoginResponseModel?>) {
                try {
                    Log.w("BTAG", "Responnse" + response.body())
                    if ((response.body()!!.success == "1")) {
                        val intent: Intent = Intent(this@ActVerifyPin, Chat_Main_activity::class.java)
                        startActivity(intent)
                        finish()
                    } else if ((response.body()!!.success == "0")) {
                        if (response.body()!!.count == 5) {
                            sessionManager!!.logout()
                        }
                        Toast.makeText(this@ActVerifyPin, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                    } else if ((response.body()!!.success == "2")) {
                        Toast.makeText(this@ActVerifyPin, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    //  pbLoading.setVisibility(View.GONE);
                } catch (e: Exception) {
                    e.printStackTrace()
                    //   pbLoading.setVisibility(View.GONE);
                }
            }

             override fun onFailure(call: Call<LoginResponseModel?>, t: Throwable) {
                // pbLoading.setVisibility(View.GONE);
                Toast.makeText(this@ActVerifyPin, "fail", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun calculateTimeDifference() {
        try {
            val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("hh:mm a")
            val date1: Date = simpleDateFormat.parse("08:00 AM")
            val date2: Date = simpleDateFormat.parse("08:05 PM")
            val difference: Long = date2.getTime() - date1.getTime()
            val days: Int = (difference / (1000 * 60 * 60 * 24)).toInt()
            var hours: Int = ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60)).toInt()
            val min: Int = (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)).toInt() / (1000 * 60)
            hours = (if (hours < 0) -hours else hours)
            Log.w("SHANTAG", "hours :: " + hours)
            Log.w("SHANTAG", "min :: " + min)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}