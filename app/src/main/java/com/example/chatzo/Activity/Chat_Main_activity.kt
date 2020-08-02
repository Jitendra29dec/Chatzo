package com.example.chatzo.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.chatzo.Fragments.Call_fragment
import com.example.chatzo.Fragments.Camera_fragment
import com.example.chatzo.Fragments.Chat_fragment
import com.example.chatzo.Fragments.Status_fragment
import com.example.chatzo.Models.ChatUserResponseModel
import com.example.chatzo.R
import com.example.chatzo.adapters.Main_viewpager_adapter
import com.example.chatzo.controllers.AppController
import com.example.chatzo.controllers.RetroFitClient
import com.example.chatzo.controllers.UserSessionManager
import com.example.chatzo.service.LinphoneService
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_chat__main_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Chat_Main_activity constructor() : AppCompatActivity() {

    var main_viewpager_adapter: Main_viewpager_adapter? = null
    var fragmentArrayList: ArrayList<Fragment>? = null
    var badge_1: BadgeDrawable? = null
    var sessionManager: UserSessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat__main_activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.action_color))
        }
        val service = Intent(this@Chat_Main_activity, LinphoneService::class.java)
        if (LinphoneService.instance == null) {
            startService(service)
        }

        sessionManager = UserSessionManager(this@Chat_Main_activity)
        fragmentArrayList = ArrayList()
        fragmentArrayList!!.add(Camera_fragment())
        fragmentArrayList!!.add(Chat_fragment())
        fragmentArrayList!!.add(Status_fragment())
        fragmentArrayList!!.add(Call_fragment())
        main_viewpager_adapter = Main_viewpager_adapter(getSupportFragmentManager(), fragmentArrayList!!)

        LinphoneService.instance?.configureAccount(sessionManager!!.userDetails[UserSessionManager.KEY_USER_ID], sessionManager!!.userDetails[UserSessionManager.KEY_USER_PIN], "14.141.116.26")
        view_pager_chat_page.setAdapter(main_viewpager_adapter)
        view_pager_chat_page.setOffscreenPageLimit(0)
        set_tab_weights()
        view_pager_chat_page.addOnPageChangeListener(TabLayoutOnPageChangeListener(tab_layout_chat_screen))
        tab_layout_chat_screen.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                view_pager_chat_page.setCurrentItem(tab.getPosition())
                //                Log.e("tab_selected","--"+tab.getPosition());
                if (tab.getPosition() == 0) {
                    AppController.camCount = 1
                    top_layout.setVisibility(View.VISIBLE)
                    tab_layout_chat_screen.getTabAt(0)!!.select()
                } else {
                    top_layout.setVisibility(View.VISIBLE)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view_pager_chat_page.setOnScrollChangeListener(object : View.OnScrollChangeListener {
                override fun onScrollChange(v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                    Log.e("scroll__", "--" + scrollX + "--" + scrollY)
                }
            })
        }
        image_search.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                search_bar.setVisibility(View.VISIBLE)
                top_bar.setVisibility(View.GONE)
            }
        })
        back_arrow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                top_bar.setVisibility(View.VISIBLE)
                search_bar.setVisibility(View.GONE)
            }
        })
        more_icon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val menu: PopupMenu = PopupMenu(this@Chat_Main_activity, v)
                menu.getMenu().add("New Group")
                menu.getMenu().add("New Broadcast")
                menu.getMenu().add("Settings")
                menu.show()
            }
        })
        tab_layout_chat_screen.getTabAt(1)!!.select()
    }

    fun set_tab_weights() {
        //SlidingTabStrip in TabLayout
        val slidingTabStrip: ViewGroup = tab_layout_chat_screen!!.getChildAt(0) as ViewGroup
        val tab0: View = slidingTabStrip.getChildAt(0)
        val layoutParams0: LinearLayout.LayoutParams = tab0.getLayoutParams() as LinearLayout.LayoutParams
        layoutParams0.weight = 1f
        //        layoutParams0.weight = 2;
        tab0.setLayoutParams(layoutParams0)

        //second tab in SlidingTabStrip
        val tab1: View = slidingTabStrip.getChildAt(1)
        val layoutParams: LinearLayout.LayoutParams = tab1.getLayoutParams() as LinearLayout.LayoutParams
        layoutParams.weight = 2f
        tab1.setLayoutParams(layoutParams)

        //second tab in SlidingTabStrip
        val tab2: View = slidingTabStrip.getChildAt(2)
        val layoutParams2: LinearLayout.LayoutParams = tab2.getLayoutParams() as LinearLayout.LayoutParams
        layoutParams2.weight = 2f
        tab2.setLayoutParams(layoutParams2)

        //second tab in SlidingTabStrip
        val tab3: View = slidingTabStrip.getChildAt(3)
        val layoutParams3: LinearLayout.LayoutParams = tab3.getLayoutParams() as LinearLayout.LayoutParams
        layoutParams3.weight = 2f
        tab1.setLayoutParams(layoutParams3)
        usersNew
    }

    override fun onResume() {
        if (AppController.camCount == 2) {
            AppController.camCount = 0
            tab_layout_chat_screen!!.getTabAt(1)!!.select()
        }
        super.onResume()
    }// pbLoading.setVisibility(View.GONE);

    //  badge_1.setNumber(1);
    private val usersNew: Unit
        private get() {
            val `object`: JsonObject = JsonObject()
            `object`.addProperty("user_id", sessionManager!!.userDetails.get("id"))
            val call: Call<ChatUserResponseModel>? = RetroFitClient.instance?.api?.getChatUserList(`object`)
            call?.enqueue(object : Callback<ChatUserResponseModel?> {
                override fun onResponse(call: Call<ChatUserResponseModel?>, response: Response<ChatUserResponseModel?>) {
                    try {
                        Log.w("BTAG", "Responnse count " + response.body()!!.allUnreadCount)
                        if ((response.body()!!.success == "1")) {
                            badge_1 = tab_layout_chat_screen!!.getTabAt(1)!!.getOrCreateBadge()
                            badge_1!!.setBackgroundColor(getResources().getColor(R.color.white))
                            badge_1!!.setBadgeTextColor(getResources().getColor(R.color.grey_text_color))
                            badge_1!!.setVisible(true)
                            response.body()!!.allUnreadCount?.toInt()?.let { badge_1!!.setNumber(it) }
                            //  badge_1.setNumber(1);
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // pbLoading.setVisibility(View.GONE);
                    }
                }

                override fun onFailure(call: Call<ChatUserResponseModel?>, t: Throwable) {}
            })
        }

    companion object {
        lateinit var tab_layout_chat_screen: TabLayout
    }
}