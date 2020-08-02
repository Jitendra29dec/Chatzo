package com.example.chatzo.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.chatzo.Models.Walkthrough_model
import com.example.chatzo.R
import com.example.chatzo.adapters.Walkthrough_adapter
import com.google.android.material.tabs.TabLayout
import java.util.*

class Walkthrough_activity constructor() : AppCompatActivity() {
    lateinit var view_pager_walkthrough: ViewPager
    lateinit var tab_layout_walkthrough: TabLayout
    var walkthrough_list: ArrayList<Walkthrough_model>? = null
   lateinit var question_mark_icon: ImageView
    lateinit var card_login: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walkthrough_activity)
        view_pager_walkthrough = findViewById(R.id.view_pager_walkthrough)
        tab_layout_walkthrough = findViewById(R.id.tab_layout_walkthrough)
        question_mark_icon = findViewById(R.id.question_mark_icon)
        card_login = findViewById(R.id.card_login)
        walkthrough_list = ArrayList()
        add_walkthrough()
        view_pager_walkthrough.setAdapter(Walkthrough_adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, walkthrough_list!!))
        tab_layout_walkthrough.setupWithViewPager(view_pager_walkthrough)
        question_mark_icon.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val menu: PopupMenu = PopupMenu(this@Walkthrough_activity, v)
                menu.getMenu().add("Help")
                menu.show()
            }
        })
        card_login.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val intent: Intent = Intent(this@Walkthrough_activity, Phone_number_activity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    fun add_walkthrough() {
        for (n in 0..2) {
            val model: Walkthrough_model = Walkthrough_model()
            if (n == 0) {
                model.heading = "Keep your data safe"
                model.text = "All your data is store encrypted on indian servers"
                model.image = R.drawable.data_safe_image
            } else if (n == 1) {
                model.heading = "Only pre-registered users can access this app"
                model.text = "Device authentication via IMEI"
                model.image = R.drawable.registered_user_image
            } else if (n == 2) {
                //model.setHeading("Remote data wipe out");
                model.heading = "Remote Data Backup/Erased"
                // model.setText("All your application data is wipe out n theft");
                model.text = "Restore Application Data From Last Sync Time in Case of Device Lost or Stolen."
                model.image = R.drawable.data_wipeout
            }
            walkthrough_list!!.add(model)
        }
    }
}