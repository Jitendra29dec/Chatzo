package com.example.chatzo.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.chatzo.Activity.Profile_info_activity
import com.example.chatzo.Models.LoginResponseModel
import com.example.chatzo.R
import com.example.chatzo.controllers.RetroFitClient
import com.example.chatzo.controllers.UserSessionManager
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

class Profile_info_activity constructor() : AppCompatActivity() {
    lateinit var card_next: CardView
    lateinit var question_mark_icon: ImageView
    lateinit var ivProfile: ImageView
    var imagedelivered: String = ""
    var sessionManager: UserSessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info_activity)
        sessionManager = UserSessionManager(this@Profile_info_activity)
        question_mark_icon = findViewById(R.id.question_mark_icon)
        card_next = findViewById(R.id.card_next)
        ivProfile = findViewById(R.id.ivProfile)
        question_mark_icon.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val menu: PopupMenu = PopupMenu(this@Profile_info_activity, v)
                menu.getMenu().add("Help")
                menu.show()
            }
        })
        ivProfile.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val i: Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i, 100)
            }
        })
        card_next.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val intent: Intent = Intent(this@Profile_info_activity, Chat_Main_activity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 100) && (resultCode == Activity.RESULT_OK) && (data != null)) {

            //getting the image Uri
            val imageUri: Uri? = data.getData()
            try {
                //getting bitmap object from uri
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri)
                ivProfile!!.setImageBitmap(bitmap)
                val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
                val imagenamedelivered: ByteArray = byteArrayOutputStream.toByteArray()
                imagedelivered = Base64.encodeToString(imagenamedelivered, Base64.NO_WRAP)
                //calling the method uploadBitmap to upload image
                updateProfile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateProfile() {
        val android_id: String = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID)
        val `object`: JsonObject = JsonObject()
        `object`.addProperty("imei", android_id)
        `object`.addProperty("profile_image", imagedelivered)
        Log.w("SHANTAG", "Object " + `object`)
        val call: Call<LoginResponseModel?>? = RetroFitClient
                .instance?.api?.profileUpdate(`object`)
        call?.enqueue(object : Callback<LoginResponseModel?> {
            public override fun onResponse(call: Call<LoginResponseModel?>, response: Response<LoginResponseModel?>) {
                try {
                    Log.w("BTAG", "Responnse" + response.body()!!.success)
                    if ((response.body()!!.success == "1")) {
                        imagedelivered = ""
                        Toast.makeText(this@Profile_info_activity, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Profile_info_activity, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            public override fun onFailure(call: Call<LoginResponseModel?>, t: Throwable) {
                Toast.makeText(this@Profile_info_activity, "fail", Toast.LENGTH_SHORT).show()
            }
        })
    }
}