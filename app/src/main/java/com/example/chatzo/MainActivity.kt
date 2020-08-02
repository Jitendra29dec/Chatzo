package com.example.chatzo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatzo.Activity.Walkthrough_activity
import com.example.chatzo.MainActivity
import com.example.chatzo.Models.LoginResponseModel
import com.example.chatzo.controllers.RetroFitClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var question_mark_icon: ImageView
    lateinit var accept_card: CardView
    var pbLoading: ProgressBar? = null
    var vP: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        question_mark_icon = findViewById(R.id.question_mark_icon)
        accept_card = findViewById(R.id.accept_card)
        pbLoading = findViewById(R.id.pbLoading)
        vP = findViewById(R.id.vP)
        question_mark_icon.setOnClickListener(View.OnClickListener { v ->
            val menu = PopupMenu(this@MainActivity, v)
            menu.menu.add("Help")
            menu.show()
        })
        accept_card.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, Walkthrough_activity::class.java)
            startActivity(intent)
            finish()

//                if (Build.VERSION.SDK_INT >= 23) {
//                    takePermissionsForMarsh();
//                } else {
//
//                    doRegularProcess();
//                }
        })
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));
//        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doRegularProcess()
                } else {
                    finish()
                }
                return
            }
        }
    }

    fun takePermissionsForMarsh() {
        Log.w("TAG", "TAKE PERMISSION ENTERED")
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE),
                    MY_PERMISSIONS)
        } else {
            doRegularProcess()
        }
    }

    private fun doRegularProcess() {
        pbLoading!!.visibility = View.VISIBLE
        vP!!.visibility = View.GONE
        val android_id = Settings.Secure.getString(contentResolver,
                Settings.Secure.ANDROID_ID)
        val `object` = JsonObject()
        `object`.addProperty("imei", android_id)
        Log.w("SHANTAG", "Object $`object`")
        val call = RetroFitClient
                .instance?.api!!.verifyDevice(`object`)
        call?.enqueue(object : Callback<LoginResponseModel?> {

            override fun onResponse(call: Call<LoginResponseModel?>, response: Response<LoginResponseModel?>) {
                try {
                    Log.w("BTAG", "Responnse" + response.body())
                    if (response.body()!!.success == "1") {
                        val intent = Intent(this@MainActivity, Walkthrough_activity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    pbLoading!!.visibility = View.GONE
                    vP!!.visibility = View.VISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                    pbLoading!!.visibility = View.GONE
                    vP!!.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<LoginResponseModel?>, t: Throwable) {
                pbLoading!!.visibility = View.GONE
                vP!!.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity, "fail", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val MY_PERMISSIONS = 65
    }
}