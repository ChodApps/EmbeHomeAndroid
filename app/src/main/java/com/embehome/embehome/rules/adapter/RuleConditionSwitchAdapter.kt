package com.embehome.embehome.rules.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import kotlinx.android.synthetic.main._select_item_switch.view.*


/** com.tronx.tt_things_app.rules
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 23-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RuleConditionSwitchAdapter(val context: Context, val board : BoardDetails, val bid : MutableLiveData<Int>, val sid : MutableLiveData<Int>, val ss : MutableLiveData<Int>, val ssno : MutableLiveData<String>) : RecyclerView.Adapter<RuleConditionSwitchAdapter.ViewHolder>() {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val root = LayoutInflater.from(context).inflate(R.layout._select_item_switch, parent, false)

        return ViewHolder(
            root
        )
    }

    override fun getItemCount(): Int {
        return board.switchesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val switch = board.switchesList[position]
        holder.itemView.select_switch.text = switch.switchName
        holder.itemView.select_switch.isChecked = getStatus(board.thing_serial_number, switch.switchId) > 0
        holder.itemView.scene_switch_checked.isChecked = isAddedToScene(board.thing_serial_number, switch.switchId)
        holder.itemView.scene_switch_checked.isEnabled = switch.switchType != "F"
        if (switch.switchType == "B" || switch.switchType == "C" ||switch.switchType == "D") {
            holder.itemView.seekBar_fan_dimmer.visibility = View.VISIBLE
            holder.itemView.textView168.visibility = View.VISIBLE
            holder.itemView.seekBar_fan_dimmer.max = if (switch.switchType == "B") 4 else if (switch.switchType == "C") 10 else if (switch.switchType == "D") 100 else 0
            holder.itemView.seekBar_fan_dimmer.progress = getStatus(board.thing_serial_number, switch.switchId) // isAddedToScene(board.thing_id, switch.switchId)
            holder.itemView.textView168.text = holder.itemView.seekBar_fan_dimmer.progress.toString()
            holder.itemView.seekBar_fan_dimmer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    holder.itemView.select_switch.isChecked = p1 != 0
                    isAddedToScene(board.thing_serial_number, switch.switchId).also {
                        if (it) ss.value = p1
                    }
                    holder.itemView.textView168.text = p1.toString()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })
        }
        else {
            holder.itemView.seekBar_fan_dimmer.visibility = View.GONE
            holder.itemView.textView168.visibility = View.GONE
        }

        if (switch.switchType == "D") {
            holder.itemView.select_switch.visibility = View.INVISIBLE
            holder.itemView.alternate_switch_name.visibility = View.VISIBLE
            holder.itemView.alternate_switch_name.text = switch.switchName

        }
        else {
            holder.itemView.alternate_switch_name.visibility = View.GONE
            holder.itemView.select_switch.visibility = View.VISIBLE
            holder.itemView.alternate_switch_name.text = ""
        }

        holder.itemView.select_switch.setOnClickListener {
            isAddedToScene(board.thing_serial_number, switch.switchId).also {
                if (it) ss.value = if (holder.itemView.select_switch.isChecked)  1  else  0
            }

            if (switch.switchType =="B" || switch.switchType == "C" ||switch.switchType == "D") {
                val ss = holder.itemView.select_switch.isChecked
                holder.itemView.seekBar_fan_dimmer.progress = if (ss) 1 else 0
            }
        }
        holder.itemView.scene_switch_checked.setOnClickListener {
            if (holder.itemView.scene_switch_checked.isChecked) {
                val ss = if (switch.switchType =="B" || switch.switchType == "C" ||switch.switchType == "D") {
                    holder.itemView.seekBar_fan_dimmer.progress
                }
                else if (holder.itemView.select_switch.isChecked)  1  else  0
                addToSelectedSwitch(board.thing_serial_number, board.thing_id, switch.switchId, ss)
                notifyDataSetChanged()
            }
            else {
                removeFromSelectedSwitch(board.thing_id, switch.switchId)
            }
        }
    }

    private fun removeFromSelectedSwitch (thingsID: Int, switchId: Int) {
        bid.value?.let { tid ->
            sid.value?.let { s ->
                if (tid == thingsID && switchId == s) {
                    bid.value = null
                    sid.value = null
                    ss.value = null
                    ssno.value = null
                }
            }
        }

    }

    private fun addToSelectedSwitch (sno : String, thingsID: Int, switchId: Int, switchStatus : Int) {
        bid.value = thingsID
        sid.value = switchId
        ss.value = switchStatus
        ssno.value = sno
    }

    fun isAddedToScene (thingsID : String, switchId : Int) : Boolean {
       ssno.value?.let { tid ->
           sid.value?.let { s ->
               if (tid == thingsID && switchId == s)
                   return true
           }
       }
        return false
    }

    fun getStatus (thingsID : String, switchId : Int) : Int {
        ssno.value?.let { tid ->
            sid.value?.let { s ->
                if (tid == thingsID && switchId == s)
                    return ss.value ?: 0
            }
        }
        return 0
    }
}