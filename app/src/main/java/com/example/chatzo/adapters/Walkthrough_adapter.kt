package com.example.chatzo.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.chatzo.Fragments.Walkthrough_fragment
import com.example.chatzo.Models.Walkthrough_model
import java.util.*

class Walkthrough_adapter(fm: FragmentManager, behavior: Int, var walkthrough_list: ArrayList<Walkthrough_model>) : FragmentPagerAdapter(fm, behavior) {
    override fun getItem(position: Int): Fragment {
        return Walkthrough_fragment(walkthrough_list[position])
    }

    override fun getCount(): Int {
        return 3
    }

}