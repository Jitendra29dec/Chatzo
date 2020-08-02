package com.example.chatzo.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.chatzo.Activity.ActCreatePin
import com.example.chatzo.Models.LoginResponseModel
import com.example.chatzo.R
import com.example.chatzo.controllers.RetroFitClient
import com.example.chatzo.controllers.UserSessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActCreatePin() : AppCompatActivity() {
    lateinit var etOne: TextInputEditText
    lateinit var etTwo: TextInputEditText
    lateinit var etThree: TextInputEditText
    lateinit var etFour: TextInputEditText
    lateinit var etCOne: TextInputEditText
    lateinit var etCTwo: TextInputEditText
    lateinit var etCThree: TextInputEditText
    lateinit var etCFour: TextInputEditText
    lateinit var cvConfirm: CardView
    var sessionManager: UserSessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_create_pin)
        sessionManager = UserSessionManager(this@ActCreatePin)
        etOne = findViewById(R.id.etOne)
        etTwo = findViewById(R.id.etTwo)
        etThree = findViewById(R.id.etThree)
        etFour = findViewById(R.id.etFour)
        etCOne = findViewById(R.id.etCOne)
        etCTwo = findViewById(R.id.etCTwo)
        etCThree = findViewById(R.id.etCThree)
        etCFour = findViewById(R.id.etCFour)
        cvConfirm = findViewById(R.id.cvConfirm)
        cvConfirm!!.setOnClickListener(View.OnClickListener {
            if ((etOne!!.getText().toString().trim { it <= ' ' } == "") || (etTwo.getText().toString().trim { it <= ' ' } == "") || (etThree.getText().toString().trim { it <= ' ' } == "") || (etFour.getText().toString().trim { it <= ' ' } == "")) {
                Toast.makeText(this@ActCreatePin, "Please enter pin", Toast.LENGTH_SHORT).show()
            } else if ((etCOne!!.getText().toString().trim { it <= ' ' } == "") || (etCTwo.getText().toString().trim { it <= ' ' } == "") || (etCThree.getText().toString().trim { it <= ' ' } == "") || (etCFour.getText().toString().trim { it <= ' ' } == "")) {
                Toast.makeText(this@ActCreatePin, "Please enter confirm pin", Toast.LENGTH_SHORT).show()
            } else if ((etOne!!.getText().toString().trim { it <= ' ' } + etTwo.getText().toString().trim { it <= ' ' } + etThree.getText().toString().trim { it <= ' ' } + etFour.getText().toString().trim { it <= ' ' }) != etCOne!!.getText().toString().trim { it <= ' ' } + etCTwo.getText().toString().trim { it <= ' ' } + etCThree.getText().toString().trim { it <= ' ' } + etCFour.getText().toString().trim { it <= ' ' }) {
                Toast.makeText(this@ActCreatePin, "Enter pin and confirm pin should be similar", Toast.LENGTH_SHORT).show()
            } else {
                pinLogs()
            }
        })
        etOne!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etOne!!.getText().toString().trim { it <= ' ' }.length == 1) {
                    etTwo!!.requestFocus()
                } else if (etOne!!.getText().toString().trim { it <= ' ' }.length == 0) {
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        etTwo!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etTwo!!.getText().toString().trim { it <= ' ' }.length == 1) {
                    etThree!!.requestFocus()
                } else if (etTwo!!.getText().toString().trim { it <= ' ' }.length == 0) {
                    etOne!!.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        etThree!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etThree!!.getText().toString().trim { it <= ' ' }.length == 1) {
                    etFour!!.requestFocus()
                } else if (etThree!!.getText().toString().trim { it <= ' ' }.length == 0) {
                    etTwo!!.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        etFour!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etFour!!.getText().toString().trim { it <= ' ' }.length == 1) {
                    etCOne!!.requestFocus()
                } else if (etFour!!.getText().toString().trim { it <= ' ' }.length == 0) {
                    etThree!!.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })


        //
        etCOne!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etCOne!!.getText().toString().trim { it <= ' ' }.length == 1) {
                    etCTwo.requestFocus()
                } else if (etCOne!!.getText().toString().trim { it <= ' ' }.length == 0) {
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        etCTwo!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etCTwo!!.getText().toString().trim { it <= ' ' }.length == 1) {
                    etCThree!!.requestFocus()
                } else if (etCTwo!!.getText().toString().trim { it <= ' ' }.length == 0) {
                    etCOne!!.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        etCThree!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etCThree!!.getText().toString().trim { it <= ' ' }.length == 1) {
                    etCThree!!.requestFocus()
                } else if (etCThree!!.getText().toString().trim { it <= ' ' }.length == 0) {
                    etCTwo!!.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        etCFour!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (etCFour!!.getText().toString().trim { it <= ' ' }.length == 1) {
                } else if (etCFour!!.getText().toString().trim { it <= ' ' }.length == 0) {
                    etCThree!!.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun pinLogs() {
        val `object` = JsonObject()
        `object`.addProperty("user_id", sessionManager!!.userDetails["id"])
        `object`.addProperty("pin", etOne!!.text.toString().trim { it <= ' ' } + etTwo!!.text.toString().trim { it <= ' ' } + etThree!!.text.toString().trim { it <= ' ' } + etFour!!.text.toString().trim { it <= ' ' })
        Log.w("SHANTAG", "Object $`object`")
        val call = RetroFitClient
                .instance?.api?.savePin(`object`)
        call?.enqueue(object : Callback<LoginResponseModel?> {
            override fun onResponse(call: Call<LoginResponseModel?>, response: Response<LoginResponseModel?>) {
                try {
                    Log.w("BTAG", "Responnse" + response.body())
                    if ((response.body()!!.success == "1")) {
                        sessionManager!!.updatePin(etOne!!.text.toString().trim { it <= ' ' } + etTwo!!.text.toString().trim { it <= ' ' } + etThree!!.text.toString().trim { it <= ' ' } + etFour!!.text.toString().trim { it <= ' ' })
                        val intent = Intent(this@ActCreatePin, Profile_info_activity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ActCreatePin, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                    }
                    //  pbLoading.setVisibility(View.GONE);
                } catch (e: Exception) {
                    e.printStackTrace()
                    //   pbLoading.setVisibility(View.GONE);
                }
            }

            override fun onFailure(call: Call<LoginResponseModel?>, t: Throwable) {
                // pbLoading.setVisibility(View.GONE);
                Toast.makeText(this@ActCreatePin, "fail", Toast.LENGTH_SHORT).show()
            }
        })
    }
}