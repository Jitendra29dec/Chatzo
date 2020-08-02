package com.example.chatzo.Activity

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.example.chatzo.R
import com.example.chatzo.service.LinphoneService
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.mediastream.Version
import java.util.*

class CallActivity : Activity() {
    // We use 2 TextureView, one for remote video and one for local camera preview
    private var mVideoView: TextureView? = null
    private var mCaptureView: TextureView? = null
    private var mCoreListener: CoreListenerStub? = null
    var card_end_button: CardView? = null
    var card_video: CardView? = null
    var card_video_inside: CardView? = null
    var card_speaker: CardView? = null
    var card_speaker_inside: CardView? = null
    var image_video: ImageView? = null
    var image_speaker: ImageView? = null
    var video = false
    var speaker = false
    var mcore: Core? = null
    var audioManager: AudioManager? = null
    var call_status: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call)
        mVideoView = findViewById(R.id.videoSurface)
        mCaptureView = findViewById(R.id.videoCaptureSurface)
        card_end_button = findViewById(R.id.card_end_button)
        image_video = findViewById(R.id.image_video)
        card_video = findViewById(R.id.card_video)
        card_video_inside = findViewById(R.id.card_video_inside)
        card_speaker = findViewById(R.id.card_speaker)
        card_speaker_inside = findViewById(R.id.card_speaker_inside)
        image_speaker = findViewById(R.id.image_speaker)
        call_status = findViewById(R.id.call_status)
        mcore = LinphoneService.core
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // We need to tell the core in which to display what
        mcore!!.nativeVideoWindowId = mVideoView
        mcore!!.nativePreviewWindowId = mCaptureView
        if (intent.extras != null) {
            video = intent.getBooleanExtra("video", false)
            set_video_card()
        }


//        Call call = mcore.getCurrentCall();
//        if(call!=null)
//        {
//            call.setMicrophoneMuted(false);
//            call.setSpeakerMuted(true);
//            //set_speaker_card();
//        }

        // Listen for call state changes
        mCoreListener = object : CoreListenerStub() {
            override fun onCallStateChanged(core: Core, call: Call, state: Call.State, message: String) {
                Log.e("state_messgae", "--$message--$state")
                if (state == Call.State.End || state == Call.State.Released) {
                    // Once call is finished (end state), terminate the activity
                    // We also check for released state (called a few seconds later) just in case
                    // we missed the first one
                    //LinphoneService.getCore().removeListener(mCoreListener);
                    finish()
                } else if (state == Call.State.OutgoingProgress) {
                    call_status?.setText("Calling...")
                } else if (state == Call.State.OutgoingRinging) {
                    call_status?.setText("Ringing...")
                } else if (state == Call.State.Connected) {
                    call_status?.setText("Call Connected")
                }
            }
        }
        card_video?.setOnClickListener(View.OnClickListener {
            if (checkAndRequestPermission(
                            Manifest.permission.CAMERA, CAMERA_TO_TOGGLE_VIDEO)) {
                video = !video
                set_video_card()
            }
        })
        card_speaker?.setOnClickListener(View.OnClickListener {
            speaker = !speaker
            set_speaker_card()
        })
        card_end_button?.setOnClickListener(View.OnClickListener {
            val core = mcore
            if (core!!.callsNb > 0) {
                var call = core.currentCall
                if (call == null) {
                    // Current call can be null if paused for example
                    call = core.calls[0]
                }
                call!!.terminate()
            } else {
                finish()
            }
        })

//        findViewById(R.id.terminate_call).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Core core = LinphoneService.getCore();
//                if (core.getCallsNb() > 0) {
//                    Call call = core.getCurrentCall();
//                    if (call == null) {
//                        // Current call can be null if paused for example
//                        call = core.getCalls()[0];
//                    }
//                    call.terminate();
//                }
//            }
//        });
    }

    override fun onStart() {
        super.onStart()
        val core = mcore
        // We need to tell the core in which to display what
        core!!.nativeVideoWindowId = mVideoView
        core.nativePreviewWindowId = mCaptureView
        checkAndRequestCallPermissions()
    }

    override fun onResume() {
        super.onResume()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        call_status!!.text = "Calling..."
        mcore!!.addListener(mCoreListener)
        resizePreview()
    }

    override fun onPause() {
        mcore!!.removeListener(mCoreListener)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @TargetApi(24)
    public override fun onUserLeaveHint() {
        // If the device supports Picture in Picture let's use it
        val supportsPip = packageManager
                .hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        org.linphone.core.tools.Log.i("[Call] Is picture in picture supported: $supportsPip")
        if (supportsPip && Version.sdkAboveOrEqual(24)) {
            enterPictureInPictureMode()
        }
    }

    fun set_speaker_card() {
        val call = mcore!!.currentCall ?: return
        if (speaker) {
            image_speaker!!.setImageResource(R.drawable.ic_baseline_volume_off_24)
            image_speaker!!.setColorFilter(resources.getColor(R.color.white))
            card_speaker_inside!!.setCardBackgroundColor(resources.getColor(R.color.background))
            audioManager!!.isSpeakerphoneOn = true
            audioManager!!.isMicrophoneMute = true
            call.microphoneMuted = true
            call.speakerMuted = false
        } else {
            image_speaker!!.setImageResource(R.drawable.ic_baseline_volume_up_24)
            image_speaker!!.setColorFilter(resources.getColor(R.color.background))
            card_speaker_inside!!.setCardBackgroundColor(resources.getColor(R.color.white))
            audioManager!!.isSpeakerphoneOn = false
            audioManager!!.isMicrophoneMute = false
            call.microphoneMuted = false
            call.speakerMuted = true
        }

//        if (call.getSpeakerMuted()) {
//
//        } else {
//
//        }
        call.update(mcore!!.createCallParams(call))
    }

    fun set_video_card() {
        val call = mcore!!.currentCall ?: return
        if (video) {
            mcore!!.enableVideoPreview(true)
            mcore!!.enableVideoDisplay(true)
            mcore!!.enableVideoCapture(true)
            mcore!!.nativeVideoWindowId = mVideoView
            mcore!!.nativePreviewWindowId = mCaptureView
            image_video!!.setImageResource(R.drawable.ic_baseline_videocam_off_24)
            image_video!!.setColorFilter(resources.getColor(R.color.white))
            card_video_inside!!.setCardBackgroundColor(resources.getColor(R.color.background))
            call.enableCamera(true)
            call.currentParams.enableVideo(true)

            //
        } else {
            mcore!!.enableVideoPreview(false)
            //mcore.enableVideoDisplay(false);
            //mcore.enableVideoCapture(false);

//            mcore.setNativeVideoWindowId(null);
//            mcore.setNativePreviewWindowId(null);
            image_video!!.setImageResource(R.drawable.ic_baseline_videocam_24)
            image_video!!.setColorFilter(resources.getColor(R.color.background))
            card_video_inside!!.setCardBackgroundColor(resources.getColor(R.color.white))

            //call.enableCamera(false);
            //call.getCurrentParams().enableVideo(false);
        }

        //mVideo.setEnabled(false);
//        if (call.getCurrentParams().videoEnabled()) {
//           // LinphoneManager.getCallManager().removeVideo();
//        } else {
//
//            //LinphoneManager.getCallManager().addVideo();
//        }
        call.update(mcore!!.createCallParams(call))
        //reinviteWithVideo();
        resizePreview()
    }

    private fun reinviteWithVideo(): Boolean {
        val core = mcore
        val call = core!!.currentCall
        if (call == null) {
            org.linphone.core.tools.Log.e("[Call Manager] Trying to add video while not in call")
            return false
        }
        if (call.remoteParams != null && call.remoteParams!!.lowBandwidthEnabled()) {
            org.linphone.core.tools.Log.e("[Call Manager] Remote has low bandwidth, won't be able to do video")
            return false
        }
        val params = core.createCallParams(call)
        if (params!!.videoEnabled()) return false

        // Check if video possible regarding bandwidth limitations
        //mBandwidthManager.updateWithProfileSettings(params);

        // Abort if not enough bandwidth...
//        if (!params.videoEnabled()) {
//            return false;
//        }

        // Not yet in video call: try to re-invite with video
        call.update(params)
        return true
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Permission not granted, won't change anything
        if (requestCode == ALL_PERMISSIONS) {
            for (index in permissions.indices) {
                val granted = grantResults[index]
                if (granted == PackageManager.PERMISSION_GRANTED) {
                    val permission = permissions[index]
                    if (Manifest.permission.RECORD_AUDIO == permission) {
                        set_speaker_card()
                    } else if (Manifest.permission.CAMERA == permission) {
                        mcore!!.reloadVideoDevices()
                    }
                }
            }
        } else {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) return
            when (requestCode) {
                CAMERA_TO_TOGGLE_VIDEO -> {
                    mcore!!.reloadVideoDevices()
                    set_video_card()
                }
                MIC_TO_DISABLE_MUTE -> set_speaker_card()
                CAMERA_TO_ACCEPT_UPDATE -> mcore!!.reloadVideoDevices()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        val granted = packageManager.checkPermission(permission, packageName)
        org.linphone.core.tools.Log.i(
                "[Permission] "
                        + permission
                        + " permission is "
                        + if (granted == PackageManager.PERMISSION_GRANTED) "granted" else "denied")
        return granted == PackageManager.PERMISSION_GRANTED
    }

    private fun checkAndRequestPermission(permission: String, result: Int): Boolean {
        if (!checkPermission(permission)) {
            org.linphone.core.tools.Log.i("[Permission] Asking for $permission")
            ActivityCompat.requestPermissions(this, arrayOf(permission), result)
            return false
        }
        return true
    }

    private fun checkAndRequestCallPermissions() {
        val permissionsList = ArrayList<String>()
        val recordAudio = packageManager
                .checkPermission(Manifest.permission.RECORD_AUDIO, packageName)
        org.linphone.core.tools.Log.i("[Permission] Record audio permission is "
                + if (recordAudio == PackageManager.PERMISSION_GRANTED) "granted" else "denied")
        val camera = packageManager.checkPermission(Manifest.permission.CAMERA, packageName)
        org.linphone.core.tools.Log.i("[Permission] Camera permission is "
                + if (camera == PackageManager.PERMISSION_GRANTED) "granted" else "denied")
        val readPhoneState = packageManager
                .checkPermission(Manifest.permission.READ_PHONE_STATE, packageName)
        org.linphone.core.tools.Log.i("[Permission] Read phone state permission is "
                + if (camera == PackageManager.PERMISSION_GRANTED) "granted" else "denied")
        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            org.linphone.core.tools.Log.i("[Permission] Asking for record audio")
            permissionsList.add(Manifest.permission.RECORD_AUDIO)
        }
        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            org.linphone.core.tools.Log.i("[Permission] Asking for read phone state")
            permissionsList.add(Manifest.permission.READ_PHONE_STATE)
        }
        val call = mcore!!.currentCall
        if (call != null && call.remoteParams != null && call.remoteParams!!.videoEnabled()) {
            if (camera != PackageManager.PERMISSION_GRANTED) {
                org.linphone.core.tools.Log.i("[Permission] Asking for camera")
                permissionsList.add(Manifest.permission.CAMERA)
            }
        }
        if (permissionsList.size > 0) {
            var permissions: Array<String?>? = arrayOfNulls(permissionsList.size)
            permissions = permissionsList.toArray(permissions)
            ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS)
        }
    }

    override fun onPictureInPictureModeChanged(
            isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        if (isInPictureInPictureMode) {
            // Currently nothing to do has we only display video
            // But if we had controls or other UI elements we should hide them
        } else {
            // If we did hide something, let's make them visible again
        }
    }

    private fun resizePreview() {
        val core = mcore
        // We need to tell the core in which to display what
        core!!.nativeVideoWindowId = mVideoView
        core.nativePreviewWindowId = mCaptureView
        if (core.callsNb > 0) {
            var call = core.currentCall
            if (call == null) {
                call = core.calls[0]
            }
            if (call == null) return
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val screenHeight = metrics.heightPixels
            val maxHeight = screenHeight / 4 // Let's take at most 1/4 of the screen for the camera preview
            var videoSize = call.currentParams
                    .sentVideoDefinition // It already takes care of rotation
            if (videoSize!!.width == 0 || videoSize.height == 0) {
                org.linphone.core.tools.Log.w(
                        "[Video] Couldn't get sent video definition, using default video definition")
                videoSize = core.preferredVideoDefinition
            }
            var width = videoSize.width
            var height = videoSize.height
            org.linphone.core.tools.Log.d("[Video] Video height is $height, width is $width")
            width = width * maxHeight / height
            height = maxHeight
            if (mCaptureView == null) {
                org.linphone.core.tools.Log.e("[Video] mCaptureView is null !")
                return
            }
            val newLp = RelativeLayout.LayoutParams(width, height)
            newLp.addRule(
                    RelativeLayout.ALIGN_PARENT_BOTTOM,
                    1) // Clears the rule, as there is no removeRule until API 17.
            newLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1)
            mCaptureView!!.layoutParams = newLp
            org.linphone.core.tools.Log.d("[Video] Video preview size set to " + width + "x" + height)
        }
    }

    companion object {
        private const val ALL_PERMISSIONS = 4
        private const val CAMERA_TO_TOGGLE_VIDEO = 0
        private const val MIC_TO_DISABLE_MUTE = 1
        private const val WRITE_EXTERNAL_STORAGE_FOR_RECORDING = 2
        private const val CAMERA_TO_ACCEPT_UPDATE = 3
    }
}