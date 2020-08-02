package com.example.chatzo.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.Activity.ActChatNew
import com.example.chatzo.Models.usersModel
import com.example.chatzo.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class AdpUser(var driverList: ArrayList<usersModel>, mContext: Context) : RecyclerView.Adapter<AdpUser.ChildHolder>(), Filterable {
    var driverListNew: ArrayList<usersModel>
    var mContext: Context
    var valueFilter: ValueFilter? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_user, parent, false)
        return ChildHolder(view)
    }

    override fun onBindViewHolder(holder: ChildHolder, position: Int) {
        val spinner = driverList[position]
        holder.tvTitle.text = spinner.name
        if (spinner.profile_pic == "") {
            val decodedString = Base64.decode(spinner.profile_pic, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.cvProfilePic.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_profile_placeholder))
            holder.llUser.setOnClickListener {
                val intent = Intent(mContext, ActChatNew::class.java)
                intent.putExtra("image", "")
                intent.putExtra("imageFlag", "")
                intent.putExtra("id", spinner.id)
                intent.putExtra("name", spinner.name)
                mContext.startActivity(intent)
            }
        } else {
            val decodedString = Base64.decode(spinner.profile_pic, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.cvProfilePic.setImageBitmap(decodedByte)
            holder.llUser.setOnClickListener {
                val intent = Intent(mContext, ActChatNew::class.java)
                intent.putExtra("imageFlag", "image")
                intent.putExtra("image", decodedString)
                intent.putExtra("id", spinner.id)
                intent.putExtra("name", spinner.name)
                mContext.startActivity(intent)
            }
        }
        //        final byte[] decodedString = Base64.decode(spinner.getProfile_pic(), Base64.DEFAULT);
//        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//        holder.cvProfilePic.setImageBitmap(decodedByte);
//
//
//
//
//
//        holder.llUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent=new Intent(mContext, ActChatNew.class);
//                intent.putExtra("image",decodedString);
//                intent.putExtra("id",spinner.getId());
//                intent.putExtra("name",spinner.getName());
//                mContext.startActivity(intent);
//            }
//        });
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
        var cvProfilePic: CircleImageView
        var llUser: LinearLayout

        init {
            llUser = itemView.findViewById<View>(R.id.llUser) as LinearLayout
            tvTitle = itemView.findViewById<View>(R.id.tvTitle) as TextView
            cvProfilePic = itemView.findViewById<View>(R.id.cvProfilePic) as CircleImageView
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            //Log.d("TAG", "constraint " + constraint );
            if (constraint != null && constraint.length > 0) {
                val filterList = ArrayList<usersModel>()
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
            driverList = results.values as ArrayList<usersModel>
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