package com.example.chatzo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.R

class Chat_adapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var chat_viewHolder: Chat_viewHolder? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_unit_view, parent, false)
        return Chat_viewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Chat_viewHolder) {
            chat_viewHolder = holder
            if (position == 0) {
                chat_viewHolder!!.card_new_message.visibility = View.GONE
                chat_viewHolder!!.user_name.text = "Rohit Singh"
                chat_viewHolder!!.user_image.setImageResource(R.drawable.people_image_1)
                chat_viewHolder!!.message_text.text = "Hi how are you"
                chat_viewHolder!!.message_time.text = "11:34 AM"
                chat_viewHolder!!.tick_image.setImageResource(R.drawable.ic_tick_read)
            } else {
                chat_viewHolder!!.card_new_message.visibility = View.VISIBLE
                chat_viewHolder!!.user_name.text = "Ravi Kumar"
                chat_viewHolder!!.user_image.setImageResource(R.drawable.people_image_2)
                chat_viewHolder!!.message_text.text = "All well, where are you?"
                chat_viewHolder!!.message_time.text = "Yesterday"
                chat_viewHolder!!.tick_image.setImageResource(R.drawable.ic_tick_unread)
            }

//            chat_viewHolder.card_chat_view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent=new Intent(context, User_chat_activity.class);
//                    intent.putExtra("name",((TextView)v.findViewById(R.id.user_name)).getText().toString());
//                    context.startActivity(intent);
//                }
//            });
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    inner class Chat_viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user_image: ImageView
        var tick_image: ImageView
        var user_name: TextView
        var message_text: TextView
        var message_time: TextView
        var card_new_message: CardView

        //   LinearLayout card_chat_view;
        init {
            card_new_message = itemView.findViewById(R.id.card_new_message)
            user_image = itemView.findViewById(R.id.user_image)
            tick_image = itemView.findViewById(R.id.image_tick)
            user_name = itemView.findViewById(R.id.user_name)
            message_time = itemView.findViewById(R.id.time_text)
            message_text = itemView.findViewById(R.id.message_text)
            //  card_chat_view=itemView.findViewById(R.id.card_chat_view);
        }
    }

}