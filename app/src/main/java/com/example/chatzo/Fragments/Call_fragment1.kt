package com.example.chatzo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.R
import com.example.chatzo.adapters.Call_adapter

class Call_fragment : Fragment() {
    lateinit  var call_recycler_view: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.call_fragment_layout, container, false)
        call_recycler_view = view.findViewById(R.id.call_recycler_view)
        call_recycler_view.setLayoutManager(LinearLayoutManager(activity))
        call_recycler_view.setAdapter(Call_adapter(activity!!))
        return view
    }
}