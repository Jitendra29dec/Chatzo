package com.example.chatzo.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.chatzo.R
import com.example.chatzo.service.LinphoneService
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub

class Incoming_call_activity : AppCompatActivity() {
    var decline_button: FrameLayout? = null
    var answer_button: FrameLayout? = null
    var contact_name: TextView? = null
    var call: Call? = null
    var mcore: Core? = null
    var connected = false
    private var mCoreListener: CoreListenerStub? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call_activity)
        decline_button = findViewById(R.id.decline_button)
        answer_button = findViewById(R.id.answer_button)
        contact_name = findViewById(R.id.contact_name)
        mcore = LinphoneService.core
        call = mcore!!.currentCall

        // mcore.addListener(mCoreListener);
        if (intent.extras != null) {
            val hh = intent.getStringExtra("user_name")
            contact_name?.setText(hh)
        }
        answer_button?.setOnClickListener(View.OnClickListener { accept_call() })
        decline_button?.setOnClickListener(View.OnClickListener { end_call() })
        mCoreListener = object : CoreListenerStub() {
            override fun onCallStateChanged(core: Core, call: Call, state: Call.State, message: String) {
                if (state == Call.State.End || state == Call.State.Released) {
                    finish()
                } else if (state == Call.State.Connected && !connected) {
                    val intent = Intent(this@Incoming_call_activity, CallActivity::class.java)
                    val params = mcore!!.createCallParams(call)
                    if (params != null) {
                        intent.putExtra("video", params.videoEnabled())
                    }
                    // As it is the Service that is starting the activity, we have to give this flag
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent)
                    connected = true
                    finish()
                }
                //super.onCallStateChanged(core, call, state, message);
            }
        }
        mcore!!.addListener(mCoreListener)
    }

    override fun onResume() {
        //mcore.addListener(mCoreListener);
        super.onResume()
    }

    fun accept_call() {
        //Call call=LinphoneService.getCore().getCurrentCall();
        // For this sample we will automatically answer incoming calls
        // mcore.addListener(mCoreListener);
        val params = mcore!!.createCallParams(call)
        //params.enableVideo(true);
        call!!.acceptWithParams(params)
    }

    fun end_call() {
        val core = mcore
        if (core!!.callsNb > 0) {
            //Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.calls[0]
            }
            call!!.terminate()
        }
        finish()
    }
}