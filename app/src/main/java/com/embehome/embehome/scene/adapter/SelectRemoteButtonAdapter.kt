package com.embehome.embehome.scene.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.embehome.embehome.R
import kotlinx.android.synthetic.main._list_name_icon.view.*


/** com.tronx.tt_things_app.scene.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 23-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SelectRemoteButtonAdapter (context : Context, val data : List<String>, val cat : String) : ArrayAdapter<String>(context, R.layout._list_name_icon, R.id.select_list_name, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)

        v.select_list_icon.setImageResource(R.drawable.irb_ac_button_auto)
        v.select_list_icon.visibility = View.INVISIBLE
        return v
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v =  super.getDropDownView(position, convertView, parent)
        v.select_list_icon.setImageResource(R.drawable.irb_ac_button_auto)
        v.select_list_icon.visibility = View.INVISIBLE
        return v
    }
}