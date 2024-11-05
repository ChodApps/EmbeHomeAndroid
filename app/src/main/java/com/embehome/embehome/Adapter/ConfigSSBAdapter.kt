package com.embehome.embehome.Adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.getSwitchImage
import kotlinx.android.synthetic.main._ssb_config_item.view.*


/** com.tronx.tt_things_app.Adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 08-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class ConfigSSBAdapter(val context : Context, val ssb : BoardDetails) : RecyclerView.Adapter<ConfigSSBAdapter.ViewHolder>() {

    val error = ArrayList<Int> ()

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout._ssb_config_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ssb.switchesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = ssb.switchesList[position]
        if (s.switchType == "E" || s.switchType == "F") {
            holder.itemView.config_switch_icon.visibility = View.INVISIBLE
            holder.itemView.config_wattage.visibility = View.INVISIBLE
            holder.itemView.textView174.visibility = View.INVISIBLE
        }
        else {
            holder.itemView.config_switch_icon.visibility = View.VISIBLE
            holder.itemView.config_wattage.visibility = View.VISIBLE
            holder.itemView.textView174.visibility = View.VISIBLE
        }
        holder.itemView.config_switch_icon.setImageResource(getSwitchImage(s.switchType))
        holder.itemView.config_switch_name.setText(s.switchName)
        holder.itemView.config_wattage.setText((s.switchWattage ?: 0).toString())
        holder.itemView.config_switch_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null && p0.isEmpty()) {
                    holder.itemView.textInputLayout13.error = "Invalid Name"
                    error.add(s.switchId)
                }
                else {
                    holder.itemView.textInputLayout13.error = ""
                    error.remove(s.switchId)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                s.switchName = p0.toString()
            }

        })

        holder.itemView.config_wattage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null && p0.isEmpty()) {
                 p0.let {
                     holder.itemView.config_wattage.hint = (0).toString()
                 }//   holder.itemView.textInputLayout12.error = "Invalid Name"
                }
//                else
//                    holder.itemView.textInputLayout12.error = ""
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 == "0")
                    holder.itemView.config_wattage.setText("")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.isEmpty()) {
                    s.switchWattage = 0
                }
                else
                    s.switchWattage = p0.toString().toInt()
            }

        })
    }
}