package com.example.chatzo.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatzo.MainActivity
import com.example.chatzo.R
import com.example.chatzo.controllers.UserSessionManager
import com.example.chatzo.service.LinphoneService

class ActSplash constructor() : AppCompatActivity() {
    var sessionManager: UserSessionManager? = null
    private var progressBar: ProgressBar? = null
    private var progressStatus: Int = 0
    private val handler: Handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_splash)
        sessionManager = UserSessionManager(this@ActSplash)
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar?
        // Start long running operation in a background thread
        if (Build.VERSION.SDK_INT >= 23) {
            takePermissionsForMarsh()
        } else {
            doRegularProcess()
        }
        Thread(object : Runnable {
            public override fun run() {
                while (progressStatus < 100) {
                    progressStatus += 5
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(object : Runnable {
                        public override fun run() {
                            progressBar!!.setProgress(progressStatus)
                        }
                    })
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }).start()
        if (LinphoneService.instance != null) {
            startService(Intent(this@ActSplash, LinphoneService::class.java))
        }
        //  doRegularProcess();
    }

    private fun doRegularProcess() {
        Handler().postDelayed(object : Runnable {
            public override fun run() {
                if (sessionManager!!.isUserLoggedIn) {
                    if ((sessionManager!!.userDetails.get("pin") == "")) {
                        startActivity(Intent(this@ActSplash, ActCreatePin::class.java))
                        finish()
                    } else {
                        // startActivity(new Intent(ActSplash.this, Chat_Main_activity.class));
                        startActivity(Intent(this@ActSplash, ActVerifyPin::class.java))
                        finish()
                    }
                } else {
                    startActivity(Intent(this@ActSplash, MainActivity::class.java))
                    finish()
                }
            }
        }, 1000)

//        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        JsonObject object = new JsonObject();
//        object.addProperty("imei", telephonyManager.getDeviceId());
//        Log.w("SHANTAG", "Object " + object);
//        Call<LoginResponseModel> call = RetroFitClient
//                .getInstance().getApi().verifyDevice(object);
//        call.enqueue(new Callback<LoginResponseModel>() {
//            @Override
//            public void onResponse(Call<LoginResponseModel> call, retrofit2.Response<LoginResponseModel> response) {
//                try {
//
//                    Log.w("BTAG", "Responnse" + response.body());
//                    if (response.body().getSuccess().equals("1")) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (sessionManager.isUserLoggedIn()) {
//
//                                    if (sessionManager.getUserDetails().get("pin").equals("")) {
//                                        startActivity(new Intent(ActSplash.this, ActCreatePin.class));
//                                        finish();
//                                    } else {
//                                        // startActivity(new Intent(ActSplash.this, Chat_Main_activity.class));
//                                        startActivity(new Intent(ActSplash.this, ActVerifyPin.class));
//                                        finish();
//                                    }
//
//
//                                } else {
//                                    startActivity(new Intent(ActSplash.this, MainActivity.class));
//                                    finish();
//                                }
//
//                            }
//                        }, 3200);
//                    } else {
//                        Toast.makeText(ActSplash.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
//
//
//                Toast.makeText(ActSplash.this, "fail", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    object IMEI {
        fun get_dev_id(ctx: Context): String {

            //Getting the Object of TelephonyManager
            val tManager: TelephonyManager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            //Getting IMEI Number of Devide
            @SuppressLint("HardwareIds") val Imei: String = tManager.getDeviceId()
            return Imei
        }
    }

    fun takePermissionsForMarsh() {
        Log.w("TAG", "TAKE PERMISSION ENTERED")
        if (((ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED
                        )) || ((ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        )) || ((ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED))) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS)
        } else {
            doRegularProcess()
        }
    }

    public override fun onRequestPermissionsResult(requestCode: Int,
                                                   permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if ((grantResults.size > 0
                                && grantResults.get(0) == PackageManager.PERMISSION_GRANTED)) {
                    doRegularProcess()
                } else {
                    finish()
                }
                return
            }
        }
    }

    companion object {
        private val MY_PERMISSIONS: Int = 65
    }
}