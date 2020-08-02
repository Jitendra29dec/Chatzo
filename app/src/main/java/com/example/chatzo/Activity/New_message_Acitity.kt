package com.example.chatzo.Activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.Models.UserModel
import com.example.chatzo.R
import com.example.chatzo.adapters.AdpUser
import com.example.chatzo.controllers.RetroFitClient
import com.example.chatzo.controllers.UserSessionManager
import com.example.chatzo.databases.UsersDb
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class New_message_Acitity constructor() : AppCompatActivity() {
   lateinit var back_button: ImageView
    lateinit var rvUsers: RecyclerView
    lateinit var tvCountContacts: TextView
    var sessionManager: UserSessionManager? = null
    lateinit var pbLoading: ProgressBar
    var usersDb: UsersDb? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message__acitity)
        sessionManager = UserSessionManager(this@New_message_Acitity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.action_color))
        }
        usersDb = UsersDb(this@New_message_Acitity)
        pbLoading = findViewById(R.id.pbLoading)
        back_button = findViewById(R.id.back_button)
        rvUsers = findViewById(R.id.rvUsers)
        tvCountContacts = findViewById(R.id.tvCountContacts)
        back_button.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressed()
            }
        })
        if (usersDb!!.userLists.size > 0) {
            pbLoading.setVisibility(View.GONE)
            rvUsers.setAdapter(AdpUser(usersDb!!.userLists, this@New_message_Acitity))
            // tvCountContacts.setText(""+(response.body().getUsers().size())+" contacts");
            tvCountContacts.setText("" + (usersDb!!.userLists.size) + " contacts")
            usersNew
        } else if (usersDb!!.userLists.size == 0) {
            users
        }
    }

    //  rvUsers.setAdapter(new AdpUser(response.body().getUsers(),New_message_Acitity.this));
    // tvCountContacts.setText(""+(response.body().getUsers().size())+" contacts");
    private val users: Unit
        private get() {
            pbLoading!!.setVisibility(View.VISIBLE)
            val `object`: JsonObject = JsonObject()
            `object`.addProperty("user_id", sessionManager!!.userDetails.get("id"))
            val call: Call<UserModel?>? = RetroFitClient
                    .instance?.api?.getUser(`object`)
            call?.enqueue(object : Callback<UserModel?> {
                 override fun onResponse(call: Call<UserModel?>, response: Response<UserModel?>) {
                    try {
                        Log.w("BTAG", "Responnse" + response.body())
                        if ((response.body()!!.success == "1")) {
                            for (i in response.body()!!.users?.indices!!) {
                                response.body()!!.users?.get(i)?.let { usersDb!!.createUsers(it) }
                            }

                            //  rvUsers.setAdapter(new AdpUser(response.body().getUsers(),New_message_Acitity.this));
                            rvUsers!!.setAdapter(AdpUser(usersDb!!.userLists, this@New_message_Acitity))
                            // tvCountContacts.setText(""+(response.body().getUsers().size())+" contacts");
                            tvCountContacts!!.setText("" + (usersDb!!.userLists.size) + " contacts")
                        } else {
                            Toast.makeText(this@New_message_Acitity, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                        }
                        pbLoading!!.setVisibility(View.GONE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        pbLoading!!.setVisibility(View.GONE)
                    }
                }

                 override fun onFailure(call: Call<UserModel?>, t: Throwable) {
                    pbLoading!!.setVisibility(View.GONE)
                    Toast.makeText(this@New_message_Acitity, "fail", Toast.LENGTH_SHORT).show()
                }
            })
        }//  pbLoading.setVisibility(View.GONE);

    //  Toast.makeText(New_message_Acitity.this, "fail", Toast.LENGTH_SHORT).show();
//  pbLoading.setVisibility(View.GONE);// Toast.makeText(New_message_Acitity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

    //  rvUsers.setAdapter(new AdpUser(response.body().getUsers(),New_message_Acitity.this));
    // tvCountContacts.setText(""+(response.body().getUsers().size())+" contacts");
    private val usersNew: Unit
        private get() {
            val `object`: JsonObject = JsonObject()
            `object`.addProperty("user_id", sessionManager!!.userDetails.get("id"))
            val call: Call<UserModel?>? = RetroFitClient
                    .instance?.api?.getUser(`object`)
            call?.enqueue(object : Callback<UserModel?> {
                 override fun onResponse(call: Call<UserModel?>, response: Response<UserModel?>) {
                    try {
                        Log.w("BTAG", "Responnse" + response.body())
                        if ((response.body()!!.success == "1")) {
                            for (i in response.body()!!.users?.indices!!) {
                                if (response.body()!!.users?.get(i)?.id?.let { usersDb!!.hasObject(it) }!!) {
                                } else {
                                    usersDb!!.createUsers(response.body()!!.users?.get(i)!!)
                                }
                            }

                            //  rvUsers.setAdapter(new AdpUser(response.body().getUsers(),New_message_Acitity.this));
                            rvUsers!!.setAdapter(AdpUser(usersDb!!.userLists, this@New_message_Acitity))
                            // tvCountContacts.setText(""+(response.body().getUsers().size())+" contacts");
                            tvCountContacts!!.setText("" + (usersDb!!.userLists.size) + " contacts")
                        } else {
                            // Toast.makeText(New_message_Acitity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //  pbLoading.setVisibility(View.GONE);
                    }
                }

                 override fun onFailure(call: Call<UserModel?>, t: Throwable) {
                    //  pbLoading.setVisibility(View.GONE);

                    //  Toast.makeText(New_message_Acitity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            })
        }
}