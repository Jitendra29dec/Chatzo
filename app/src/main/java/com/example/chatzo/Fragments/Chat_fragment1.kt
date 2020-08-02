package com.example.chatzo.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.Activity.New_message_Acitity
import com.example.chatzo.Models.ChatUserResponseModel
import com.example.chatzo.R
import com.example.chatzo.adapters.AdpChatUser
import com.example.chatzo.controllers.RetroFitClient
import com.example.chatzo.controllers.UserSessionManager
import com.example.chatzo.databases.ChatUsersDb
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Chat_fragment : Fragment() {
    lateinit var recycler_view_chat: RecyclerView
    lateinit var card_new_message: CardView
    lateinit var pbLoading: ProgressBar
    lateinit var sessionManager: UserSessionManager


    lateinit var chatUsersDb: ChatUsersDb

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.chat_fragment_layout, container, false)
        recycler_view_chat = view.findViewById(R.id.recycler_view_chat)
        card_new_message = view.findViewById(R.id.card_new_message)
        pbLoading = view.findViewById(R.id.pbLoading)
        sessionManager = UserSessionManager(activity)
        chatUsersDb = ChatUsersDb(activity)

        card_new_message.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, New_message_Acitity::class.java)
            startActivity(intent)
        })

        if (chatUsersDb.userLists.size > 0) {
            pbLoading.setVisibility(View.GONE)
            recycler_view_chat.setAdapter(AdpChatUser(chatUsersDb.userLists, activity!!))
            usersNew
        } else if (chatUsersDb.userLists.size == 0) {
            users
        }
        
        return view
    }



    private val users: Unit
        private get() {
            pbLoading!!.visibility = View.VISIBLE

            val `object` = JsonObject()
            `object`.addProperty("user_id", sessionManager.userDetails["id"])



            val call: Call<ChatUserResponseModel> = RetroFitClient.Companion.instance?.api!!.getChatUserList(`object`)
            call.enqueue(object : Callback<ChatUserResponseModel> {
                override fun onResponse(call: Call<ChatUserResponseModel>, response: Response<ChatUserResponseModel>) {
                    try {
                        Log.w("BTAG", "Responnse" + response.body())
                        if (response.body()!!.success == "1") {
                            for (i in response.body()?.chatsUsers!!.indices) {
                                chatUsersDb!!.createUsers(response.body()?.chatsUsers!![i])
                            }

                            recycler_view_chat!!.adapter = AdpChatUser(chatUsersDb.userLists, activity!!)
                        } else {
                            Toast.makeText(activity, response?.body()?.msg, Toast.LENGTH_SHORT).show()
                        }
                        pbLoading!!.visibility = View.GONE
                    } catch (e: Exception) {
                        e.printStackTrace()
                        pbLoading!!.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<ChatUserResponseModel>, t: Throwable) {
                    pbLoading!!.visibility = View.GONE
                    Toast.makeText(activity, "fail", Toast.LENGTH_SHORT).show()
                }
            })
        }


    private val usersNew: Unit
        private get() {

            val `object` = JsonObject()
            `object`.addProperty("user_id", sessionManager.userDetails["id"])

            val call: Call<ChatUserResponseModel> = RetroFitClient.Companion.instance?.api!!.getChatUserList(`object`)

            call.enqueue(object : Callback<ChatUserResponseModel> {
                override fun onResponse(call: Call<ChatUserResponseModel>, response: Response<ChatUserResponseModel>) {
                    try {
                        Log.w("BTAG", "Responnse" + response.body())
                        if (response.body()?.success== "1") {
                            for (i in response.body()?.chatsUsers!!.indices) {
                                if (chatUsersDb!!.hasObject(response.body()!!.chatsUsers!![i].user_id!!)) {
                                } else {
                                    chatUsersDb!!.createUsers(response.body()!!.chatsUsers!![i])
                                }
                            }

                            //  recycler_view_chat.setAdapter(new AdpChatUser(response.body().getChatsUsers(),getActivity()));
                            recycler_view_chat!!.adapter = AdpChatUser(chatUsersDb.userLists, activity!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // pbLoading.setVisibility(View.GONE);
                    }
                }

                override fun onFailure(call: Call<ChatUserResponseModel>, t: Throwable) {}
            })
        }
}