package com.example.chatzo.adapters

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.Activity.ActViewPdf
import com.example.chatzo.Models.MessagesModel
import com.example.chatzo.R
import java.io.*
import java.util.*

class AdpMessages(var driverList: ArrayList<MessagesModel>, mContext: Context) : RecyclerView.Adapter<AdpMessages.ChildHolder>(), Filterable {
    var driverListNew: ArrayList<MessagesModel>
    var mContext: Context
    var valueFilter: ValueFilter? = null
    fun addItem(datum: MessagesModel) {
        driverList.add(datum)
        notifyItemInserted(driverList.size)
        //  ActChatNew.sizeList=driverList.size();
        // notifyDataSetChanged();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_messages, parent, false)
        return ChildHolder(view)
    }

    override fun onBindViewHolder(holder: ChildHolder, position: Int) {
        val messagesModel = driverList[position]
        if (messagesModel.side == "Left") {
            if (messagesModel.message_status == "Sent" || messagesModel.message_status == "Delivered") {
                holder.image_tick_right.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_tick_unread))
            } else if (messagesModel.message_status == "Seen") {
                holder.image_tick_right.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_tick_read))
            }
            holder.rlleft.visibility = View.GONE
            holder.rlRight.visibility = View.VISIBLE
            holder.send_msg_right.text = messagesModel.message
            holder.send_time_right.text = messagesModel.sent_time
            if (messagesModel.media_type == "Document") {
                holder.send_msg_right.visibility = View.GONE
                holder.ivPdfRight.visibility = View.VISIBLE
                holder.ivPdfRight.setOnClickListener {
                    try {
                        // File sdDir = android.os.Environment.getExternalStorageDirectory();
                        val sdDir = Environment.getExternalStorageDirectory()
                        val dir = File(sdDir, "/" + messagesModel.file_name)
                        if (!dir.exists()) {
                            val dec = Base64.getDecoder()
                            val strdec = dec.decode(messagesModel.file_data)
                            val out: OutputStream = FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/" + messagesModel.file_name)
                            out.write(strdec)
                            out.close()
                            mContext.startActivity(Intent(mContext, ActViewPdf::class.java).putExtra("filename", Environment.getExternalStorageDirectory().toString() + "/" + messagesModel.file_name).putExtra("name", messagesModel.file_name))
                        } else {
                            mContext.startActivity(Intent(mContext, ActViewPdf::class.java).putExtra("filename", Environment.getExternalStorageDirectory().toString() + "/" + messagesModel.file_name).putExtra("name", messagesModel.file_name))
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else if (messagesModel.media_type == "Text") {
                holder.send_msg_right.visibility = View.VISIBLE
                holder.ivPdfRight.visibility = View.GONE
            }
        } else if (messagesModel.side == "Right") {
            holder.rlleft.visibility = View.VISIBLE
            holder.rlRight.visibility = View.GONE
            holder.send_msg_left.text = messagesModel.message
            holder.send_time_left.text = messagesModel.sent_time
            if (messagesModel.media_type == "Document") {
                holder.send_msg_left.visibility = View.GONE
                holder.ivPdfLeft.visibility = View.VISIBLE
                holder.ivPdfLeft.setOnClickListener {
                    try {
                        // File sdDir = android.os.Environment.getExternalStorageDirectory();
                        val sdDir = Environment.getExternalStorageDirectory()
                        val dir = File(sdDir, "/" + messagesModel.file_name)
                        if (!dir.exists()) {
                            val dec = Base64.getDecoder()
                            val strdec = dec.decode(messagesModel.file_data)
                            val out: OutputStream = FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/" + messagesModel.file_name)
                            out.write(strdec)
                            out.close()
                            mContext.startActivity(Intent(mContext, ActViewPdf::class.java).putExtra("filename", Environment.getExternalStorageDirectory().toString() + "/" + messagesModel.file_name).putExtra("name", messagesModel.file_name))
                        } else {
                            mContext.startActivity(Intent(mContext, ActViewPdf::class.java).putExtra("filename", Environment.getExternalStorageDirectory().toString() + "/" + messagesModel.file_name).putExtra("name", messagesModel.file_name))
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else if (messagesModel.media_type == "Text") {
                holder.send_msg_left.visibility = View.VISIBLE
                holder.ivPdfLeft.visibility = View.GONE
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
        var rlleft: RelativeLayout
        var rlRight: RelativeLayout
        var send_msg_right: TextView
        var send_time_right: TextView
        var send_msg_left: TextView
        var send_time_left: TextView
        var ivPdfLeft: ImageView
        var ivPdfRight: ImageView
        var image_tick_right: ImageView

        init {
            rlRight = itemView.findViewById(R.id.rlRight)
            rlleft = itemView.findViewById(R.id.rlleft)
            send_time_left = itemView.findViewById(R.id.send_time_left)
            send_msg_left = itemView.findViewById(R.id.send_msg_left)
            send_msg_right = itemView.findViewById(R.id.send_msg_right)
            send_time_right = itemView.findViewById(R.id.send_time_right)
            ivPdfRight = itemView.findViewById(R.id.ivPdfRight)
            ivPdfLeft = itemView.findViewById(R.id.ivPdfLeft)
            image_tick_right = itemView.findViewById(R.id.image_tick_right)
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            //Log.d("TAG", "constraint " + constraint );
            if (constraint != null && constraint.length > 0) {
                val filterList = ArrayList<MessagesModel>()
                for (i in driverList.indices) {
                    if (driverList[i].message!!.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            driverList = results.values as ArrayList<MessagesModel>
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