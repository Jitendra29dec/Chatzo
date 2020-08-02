package com.example.chatzo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.R

class Call_adapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var cholder: Call_viewholder? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.call_unit_view, parent, false)
        return Call_viewholder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Call_viewholder) {
            cholder = holder
            if (position == 0) {
                cholder!!.user_name.text = "Emma office"
                cholder!!.user_image.setImageResource(R.drawable.people_image_3)
                cholder!!.message_text.text = "Yesterday, 6:46 pm"
                cholder!!.image_arrow.setImageResource(R.drawable.ic_baseline_call_made_24)
                cholder!!.call_icon.setImageResource(R.drawable.ic_call_button)
                cholder!!.image_arrow.setColorFilter(context.resources.getColor(R.color.green))
            } else {
                cholder!!.user_name.text = "Ravi Kumar"
                cholder!!.user_image.setImageResource(R.drawable.people_image_2)
                cholder!!.message_text.text = "Yesterday, 8:46 pm"
                cholder!!.image_arrow.setImageResource(R.drawable.ic_baseline_call_received_24)
                cholder!!.call_icon.setImageResource(R.drawable.ic_video_camera)
                cholder!!.image_arrow.setColorFilter(context.resources.getColor(R.color.red))
            }
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    inner class Call_viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user_image: ImageView
        var image_arrow: ImageView
        var call_icon: ImageView
        var user_name: TextView
        var message_text: TextView

        init {
            call_icon = itemView.findViewById(R.id.call_icon)
            image_arrow = itemView.findViewById(R.id.image_arrow)
            user_image = itemView.findViewById(R.id.user_image)
            user_name = itemView.findViewById(R.id.user_name)
            message_text = itemView.findViewById(R.id.message_text)
        }
    }

}