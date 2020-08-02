package com.example.chatzo.Activity

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.chatzo.Activity.PhotoVideoRedirectActivity
import com.example.chatzo.R

/**
 * Created by sotsys016-2 on 13/8/16 in com.cnc3camera.
 */
class PhotoVideoRedirectActivity constructor() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photovideo_redirect)
        init()
    }

    var videoView: VideoView? = null
    private fun init() {
        val imgShow: ImageView = findViewById<View>(R.id.imgShow) as ImageView
        videoView = findViewById<View>(R.id.vidShow) as VideoView?
        if (getIntent().getStringExtra("WHO").equals("Image", ignoreCase = true)) {
            imgShow.setVisibility(View.VISIBLE)
            Glide.with(this@PhotoVideoRedirectActivity)
                    .load(getIntent().getStringExtra("PATH"))
                    .listener(object : RequestListener<String?, GlideDrawable?> {
                        public override fun onException(e: Exception, model: String?, target: Target<GlideDrawable?>, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        public override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable?>, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    }).placeholder(R.drawable.ic_photo_cont)
                    .into(imgShow)
        } else {
            videoView!!.setVisibility(View.VISIBLE)
            try {
                videoView!!.setMediaController(null)
                videoView!!.setVideoURI(Uri.parse(getIntent().getStringExtra("PATH")))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            videoView!!.requestFocus()
            //videoView.setZOrderOnTop(true);
            videoView!!.setOnPreparedListener(object : OnPreparedListener {
                public override fun onPrepared(mp: MediaPlayer) {
                    videoView!!.start()
                }
            })
            videoView!!.setOnCompletionListener(object : OnCompletionListener {
                public override fun onCompletion(mp: MediaPlayer) {
                    videoView!!.start()
                }
            })
        }
    }

    public override fun onBackPressed() {
        if (videoView!!.isPlaying()) {
            videoView!!.pause()
        }
        super.onBackPressed()
    }
}