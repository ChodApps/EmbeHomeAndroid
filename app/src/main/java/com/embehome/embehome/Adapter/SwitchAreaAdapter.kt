package com.embehome.embehome.Adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.SwitchDetails
import com.embehome.embehome.R
import com.embehome.embehome.getSwitchImages
import kotlinx.android.synthetic.main._individual_switch.view.*


/** com.tronx.tt_things_app.Adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 28-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SwitchAreaAdapter (val context : Context, val data : MutableLiveData<ArrayList<SwitchDetails>>, val selectedSwitch : MutableLiveData<Int>, val switchDetailView : MutableLiveData<Int>) : RecyclerView.Adapter<SwitchAreaAdapter.ViewHolder>() {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout._individual_switch, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val switch = data.value!![position]
        holder.itemView.individual_switch_name.text = getName(switch.switchName)
//        if(data.value!![position].switchstatus == 0) {
//            holder.itemView.image_grid_image.setImageResource(R.drawable.bulb_off)
//        }
//        else {
//            holder.itemView.image_grid_image.setImageResource(R.drawable.bulb_on)
//        }
        val res = getSwitchImages(switch.switchIconId, switch.switchstatus > 0)

if(switch.switchstatus>0){
    holder.itemView.switch_parent_layout.setBackgroundColor(context.getColor(R.color.blueSelected))
    holder.itemView.textView30.setBackgroundColor(context.getColor(R.color.white))
    holder.itemView.individual_switch_name.setTextColor(context.getColor(R.color.white))

}else{
    holder.itemView.switch_parent_layout.setBackgroundColor(context.getColor(R.color.white))
    holder.itemView.textView30.setBackgroundColor(context.getColor(R.color.primaryText))
    holder.itemView.individual_switch_name.setTextColor(context.getColor(R.color.primaryText))

}

        holder.itemView.image_grid_image.setImageResource(res)

        holder.itemView.image_grid_image.setOnClickListener {
            if (switch.switchType != "F")
            selectedSwitch.value = position
        }
        holder.itemView.individual_switch_name.setOnClickListener {
            if (switch.switchType != "E" && switch.switchType != "F")
            switchDetailView.value = position

        }
        if (switchDetailView.value == position) {
            holder.itemView.switchSelectOutline1.setBackgroundColor(context.getColor(R.color.colorAccent))
            holder.itemView.switchSelectOutline2.setBackgroundColor(context.getColor(R.color.colorAccent))
            holder.itemView.switchSelectOutline3.setBackgroundColor(context.getColor(R.color.colorAccent))
            holder.itemView.switchSelectOutline4.setBackgroundColor(context.getColor(R.color.colorAccent))

            holder.itemView.imageView274.visibility = View.VISIBLE
        }
        else {
            holder.itemView.switchSelectOutline1.setBackgroundColor(Color.TRANSPARENT)
            holder.itemView.switchSelectOutline2.setBackgroundColor(Color.TRANSPARENT)
            holder.itemView.switchSelectOutline3.setBackgroundColor(Color.TRANSPARENT)
            holder.itemView.switchSelectOutline4.setBackgroundColor(Color.TRANSPARENT)



            holder.itemView.imageView274.visibility = View.INVISIBLE
        }
    }

    private fun getName (name : String) : String {
        return try {
            if (name.length >= 14)
                name
            else {
                val size = (14 - name.length) / 2
                "${" ".repeat(size)}$name${" ".repeat(size)}"
            }
        } catch (e : Exception) {
            Log.e ("TronX_Switch", "Switch Adapter ${e.message}")
            name
        }
    }

    private fun getResource (type : String, status : Int) : Int {
       return when (type) {
            "A" -> {
                if (status == 0)
                    R.drawable.bulb_off
                else
                    R.drawable.bulb_on
            }

            "B" -> {
                if (status == 0)
                    R.drawable.tt_device_disable_ceilingfan
                else
                    R.drawable.tt_device_enable_ceilingfan

            }

           "C" -> {
               if (status > 0)
                   R.drawable.tt_device_enable_led
               else
                   R.drawable.tt_device_disable_led
           }

           "E" -> {
               R.drawable.bottom_navigation_remote
           }
           else -> {
               R.drawable.bulb_off
           }
        }
    }
}