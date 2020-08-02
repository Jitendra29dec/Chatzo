package com.example.chatzo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.chatzo.Models.Walkthrough_model
import com.example.chatzo.R

class Walkthrough_fragment(var model: Walkthrough_model) : Fragment() {
    lateinit var text_heading: TextView
    lateinit var text_data: TextView
    lateinit var image_walkthrough: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.walkthrough_frgament_layout, container, false)
        image_walkthrough = view.findViewById(R.id.image_walkthrough)
        text_data = view.findViewById(R.id.text_data)
        text_heading = view.findViewById(R.id.text_heading)
        image_walkthrough.setImageResource(model.image)
        text_heading.setText(model.heading)
        text_data.setText(model.text)
        return view
    }

}