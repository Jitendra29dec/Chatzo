package com.example.chatzo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.Models.Message_model
import com.example.chatzo.R
import java.util.*

class User_chat_adapter(var context: Context, var message_list: ArrayList<Message_model>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var cholder: User_chat_viewholder? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(context).inflate(R.layout.message_right_layout, parent, false)
            User_chat_viewholder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.message_left_layout, parent, false)
            User_chat_viewholder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is User_chat_viewholder) {
            cholder = holder
            if (position == 0) {
                cholder!!.arrow_triangle.visibility = View.VISIBLE
            } else {
                if (message_list[position].isSelf) {
                    if (message_list[position - 1].isSelf) {
                        cholder!!.arrow_triangle.visibility = View.INVISIBLE
                    } else {
                        cholder!!.arrow_triangle.visibility = View.VISIBLE
                    }
                } else {
                    if (message_list[position - 1].isSelf) {
                        cholder!!.arrow_triangle.visibility = View.VISIBLE
                    } else {
                        cholder!!.arrow_triangle.visibility = View.INVISIBLE
                    }
                }
            }
            cholder!!.send_msg.text = message_list[position].message_text
            cholder!!.send_time.text = message_list[position].message_time
            if (message_list[position].isRead) {
                cholder!!.tick_image.setImageResource(R.drawable.ic_tick_read)
            } else {
                cholder!!.tick_image.setImageResource(R.drawable.ic_tick_unread)
            }
        }
    }

    override fun getItemCount(): Int {
        return message_list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (message_list[position].isSelf) {
            0
        } else {
            1
        }
    }

    inner class User_chat_viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var arrow_triangle: ImageView
        var tick_image: ImageView
        var send_msg: TextView
        var send_time: TextView

        init {
            arrow_triangle = itemView.findViewById(R.id.test_arrow)
            tick_image = itemView.findViewById(R.id.image_tick)
            send_time = itemView.findViewById(R.id.send_time)
            send_msg = itemView.findViewById(R.id.send_msg)
        }
    }

}