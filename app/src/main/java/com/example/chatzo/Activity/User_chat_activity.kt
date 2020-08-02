package com.example.chatzo.Activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.Models.Message_model
import com.example.chatzo.R
import com.example.chatzo.adapters.User_chat_adapter
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_chat_activity.*

class User_chat_activity constructor() : AppCompatActivity() {

    var user_chat_adapter: User_chat_adapter? = null
    var message_list: ArrayList<Message_model>? = null
    var show_attachement: Boolean = false
    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_chat_activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.action_color))
        }

        if (getIntent().getExtras() != null) {
            name = getIntent().getStringExtra("name")
            user_name.setText(name)
            when (name!!.toLowerCase()) {
                "ravi kumar" -> user_image.setImageResource(R.drawable.people_image_2)
                "rohit singh" -> user_image.setImageResource(R.drawable.people_image_1)
            }
        }

        chat_rv.setLayoutManager(LinearLayoutManager(this))

        message_list = ArrayList()
        user_chat_adapter = User_chat_adapter(this, message_list!!)
        chat_rv.setAdapter(user_chat_adapter)
        set_message_list()

        back_button.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressed()
            }
        })
        attachment_icon.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                if (show_attachement) {
                    attachment_layout.setVisibility(View.GONE)
                } else {
                    attachment_layout.setVisibility(View.VISIBLE)
                }
                show_attachement = !show_attachement
            }
        })
    }

    fun set_message_list() {
        for (k in 0..3) {
            val model: Message_model = Message_model()
            when (k) {
                0 -> {
                    model.isSelf = false
                    model.isRead = false
                    model.message_time = "5:43 pm"
                    model.message_text = "Hello, How are you"
                }
                1 -> {
                    model.isSelf = false
                    model.isRead = false
                    model.message_time = "5:53 pm"
                    model.message_text = "Have you completed the task"
                }
                2 -> {
                    model.isSelf = true
                    model.isRead = true
                    model.message_time = "6:01 pm"
                    model.message_text = "All well sir"
                }
                3 -> {
                    model.isSelf = true
                    model.isRead = false
                    model.message_time = "6:02 pm"
                    model.message_text = "Yes task detail are on your email"
                }
            }
            message_list!!.add(model)
        }
        user_chat_adapter!!.notifyDataSetChanged()
    }
}