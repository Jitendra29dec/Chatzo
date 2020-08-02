package com.example.chatzo.Fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatzo.Activity.RunTimePermission
import com.example.chatzo.Activity.RunTimePermission.RunTimePermissionListener
import com.example.chatzo.Activity.WhatsappCameraActivity
import com.example.chatzo.R
import com.example.chatzo.controllers.AppController

class Camera_fragment : Fragment() {
    private var runTimePermission: RunTimePermission? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.camera_fragment_layout, container, false)
        if (AppController.Companion.camCount == 1) {
            runTimePermission = RunTimePermission(activity!!)
            runTimePermission!!.requestPermission(arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), object : RunTimePermissionListener {
                override fun permissionGranted() {
                    // First we need to check availability of play services
                    startActivity(Intent(activity, WhatsappCameraActivity::class.java))
                }

                override fun permissionDenied() {
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            })
        }
        return view
    }
}