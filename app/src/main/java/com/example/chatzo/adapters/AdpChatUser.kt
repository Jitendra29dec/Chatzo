package com.example.chatzo.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.Activity.ActChatNew
import com.example.chatzo.Models.ChatUserListModel
import com.example.chatzo.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class AdpChatUser(var driverList: ArrayList<ChatUserListModel>, mContext: Context) : RecyclerView.Adapter<AdpChatUser.ChildHolder>(), Filterable {
    var driverListNew: ArrayList<ChatUserListModel>
    var mContext: Context
    var valueFilter: ValueFilter? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_unit_view, parent, false)
        return ChildHolder(view)
    }

    override fun onBindViewHolder(holder: ChildHolder, position: Int) {
        val spinner = driverList[position]
        holder.tvTitle.text = spinner.name
        holder.message_text.text = spinner.message
        holder.time_text.text = spinner.sent_date
        if (spinner.message_status == "Sent" || spinner.message_status == "Delivered") {
            holder.image_tick.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_tick_unread))
        } else if (spinner.message_status == "Seen") {
            holder.image_tick.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_tick_read))
        }
        if (spinner.profile_pic == "") {
            val decodedString = Base64.decode(spinner.profile_pic, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.cvProfilePic.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_profile_placeholder))
            holder.llUser.setOnClickListener {
                val intent = Intent(mContext, ActChatNew::class.java)
                intent.putExtra("image", "")
                intent.putExtra("imageFlag", "")
                intent.putExtra("id", spinner.user_id)
                intent.putExtra("name", spinner.name)
                mContext.startActivity(intent)
            }
        } else {
            val decodedString = Base64.decode(spinner.profile_pic, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.cvProfilePic.setImageBitmap(decodedByte)
            holder.llUser.setOnClickListener {
                val intent = Intent(mContext, ActChatNew::class.java)
                intent.putExtra("image", decodedString)
                intent.putExtra("imageFlag", "image")
                intent.putExtra("id", spinner.user_id)
                intent.putExtra("name", spinner.name)
                mContext.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return driverList.size
    }

    override fun getFilter(): Filter {
        valueFilter = ValueFilter()
        return valueFilter!!
    }

    inner class ChildHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var message_text: TextView
        var time_text: TextView
        var cvProfilePic: CircleImageView
        var image_tick: ImageView
        var llUser: LinearLayout

        init {
            llUser = itemView.findViewById<View>(R.id.llUser) as LinearLayout
            tvTitle = itemView.findViewById<View>(R.id.user_name) as TextView
            time_text = itemView.findViewById<View>(R.id.time_text) as TextView
            message_text = itemView.findViewById<View>(R.id.message_text) as TextView
            cvProfilePic = itemView.findViewById<View>(R.id.user_image) as CircleImageView
            image_tick = itemView.findViewById<View>(R.id.image_tick) as ImageView
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            //Log.d("TAG", "constraint " + constraint );
            if (constraint != null && constraint.length > 0) {
                val filterList = ArrayList<ChatUserListModel>()
                for (i in driverList.indices) {
                    if (driverList[i].name!!.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterList.add(driverList[i])
                    }
                }
                results.count = filterList.size
                results.values = filterList
            } else {
                results.count = driverListNew.size
                results.values = driverListNew
            }
            return results
        }

        override fun publishResults(constraint: CharSequence,
                                    results: FilterResults) {
            driverList = results.values as ArrayList<ChatUserListModel>
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    init {
        driverListNew = driverList
        this.mContext = mContext
    }
}