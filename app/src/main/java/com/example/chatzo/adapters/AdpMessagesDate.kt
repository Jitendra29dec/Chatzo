package com.example.chatzo.adapters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.Activity.ActChatNew
import com.example.chatzo.Models.MessageRsponseDataModel
import com.example.chatzo.R
import java.util.*

class AdpMessagesDate(var driverList: ArrayList<MessageRsponseDataModel>, mContext: Context) : RecyclerView.Adapter<AdpMessagesDate.ChildHolder>(), Filterable {
    var driverListNew: ArrayList<MessageRsponseDataModel>
    var mContext: Context
    var valueFilter: ValueFilter? = null
    var adpMessages: AdpMessages? = null
    fun addItem(datum: MessageRsponseDataModel) {
        driverList.add(datum)
        notifyItemInserted(driverList.size)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_message_date, parent, false)
        return ChildHolder(view)
    }

    override fun onBindViewHolder(holder: ChildHolder, position: Int) {
        val messageRsponseDataModel = driverList[position]
        holder.tvDate.text = messageRsponseDataModel.date
        adpMessages = AdpMessages(messageRsponseDataModel?.chats!!, mContext)
        holder.chat_rv.adapter = adpMessages
        Handler().postDelayed({
            if (position == driverList.size - 1) {
                if (ActChatNew.firstTime == "3") {
                    adpMessages!!.addItem(ActChatNew.messagesModel)
                    //
                    Handler().postDelayed({ ActChatNew.svChat!!.post { ActChatNew.svChat!!.fullScroll(View.FOCUS_DOWN) } }, 100)
                    //
                }
            }
        }, 100)

//        holder.chat_rv.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (position==driverList.size()-1){
//                                        if (ActChatNew.firstTime.equals("3")){
//                                           // adpMessages.addItem(ActChatNew.messagesModel);
//                                            holder.chat_rv.scrollToPosition(ActChatNew.listSizeNew);
//                                          //  holder.chat_rv.smoothScrollToPosition(adpMessages.getItemCount()-1);
//                                        }
//                                    }
//
//
//                                }
//                            });
    }

    override fun getItemCount(): Int {
        return driverList.size
    }

    override fun getFilter(): Filter {
        valueFilter = ValueFilter()
        return valueFilter!!
    }

    inner class ChildHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDate: TextView
        var chat_rv: RecyclerView

        init {
            tvDate = itemView.findViewById(R.id.tvDate)
            chat_rv = itemView.findViewById(R.id.chat_rv)
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            //Log.d("TAG", "constraint " + constraint );
            if (constraint != null && constraint.length > 0) {
                val filterList = ArrayList<MessageRsponseDataModel>()
                for (i in driverList.indices) {
                    if (driverList[i].date!!.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            driverList = results.values as ArrayList<MessageRsponseDataModel>
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