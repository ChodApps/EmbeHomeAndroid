package com.embehome.embehome.scene.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.SceneSwitchDetailModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import kotlinx.android.synthetic.main._select_item_switch.view.*


/** com.tronx.tt_things_app.scene.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 05-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SceneSwitchAdapter(val context: Context, val board : BoardDetails, val selectedBoard : MutableLiveData<ArrayList<SceneSwitchDetailModel>>, val twoWay : ArrayList<Int>) : RecyclerView.Adapter<SceneSwitchAdapter.ViewHolder>() {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }


    var twoWayFlag = false


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
        if (switch.twoway_id != null) {
            holder.itemView.select_item_switch_parent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.twoWayShade))
        }
        else {
            holder.itemView.select_item_switch_parent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.thinLayerOverImage))
        }
        holder.itemView.select_switch.isChecked = isAddedToScene(board.thing_id, switch.switchId)?.switchstatus ?: 0 > 0
        holder.itemView.scene_switch_checked.isChecked = isAddedToScene(board.thing_id, switch.switchId) != null
        if (switch.switchType == "B" || switch.switchType == "C" ||switch.switchType == "D") {
            holder.itemView.seekBar_fan_dimmer.visibility = View.VISIBLE
            holder.itemView.textView168.visibility = View.VISIBLE
            holder.itemView.seekBar_fan_dimmer.max = if (switch.switchType == "B") 4 else if (switch.switchType == "C") 10 else if (switch.switchType == "D") 100 else 0
            holder.itemView.seekBar_fan_dimmer.progress = isAddedToScene(board.thing_id, switch.switchId)?.switchstatus ?: 0
            holder.itemView.textView168.text = holder.itemView.seekBar_fan_dimmer.progress.toString()
            holder.itemView.seekBar_fan_dimmer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    holder.itemView.select_switch.isChecked = p1 != 0
                    isAddedToScene(board.thing_id, switch.switchId)?.switchstatus = p1
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
            val s = isAddedToScene(board.thing_id, switch.switchId)
            s?.switchstatus = if (holder.itemView.select_switch.isChecked)  1  else  0
            if (switch.switchType =="B" || switch.switchType == "C" ||switch.switchType == "D") {
                val ss = holder.itemView.select_switch.isChecked
                holder.itemView.seekBar_fan_dimmer.progress = if (ss) 1 else 0
            }
        }
        holder.itemView.scene_switch_checked.setOnClickListener {
            if (holder.itemView.scene_switch_checked.isChecked) {
                if (switch.twoway_id != null) {
                    switch.twoway_id?.let {
                        try {
                            val id = it.toInt()
                            twoWay.contains(id).also {
                                if (it) {
                                    AppDialogs.openMessageDialog(context, "This switch is configured as Two way and one switch is added already in this scene. You cannot add this switch.")
                                    twoWayFlag = true
                                    holder.itemView.scene_switch_checked.isChecked = false
                                }
                                else {
                                    twoWay.add(id)
                                    val ss = if (switch.switchType =="B" || switch.switchType == "C" ||switch.switchType == "D") {
                                        holder.itemView.seekBar_fan_dimmer.progress
                                    }
                                    else if (holder.itemView.select_switch.isChecked)  1  else  0
                                    addToSeletedSwitch(board.thing_serial_number, board.thing_id, switch.switchId, ss)
                                }
                            }

                        }
                        catch (e : Exception) {
                            Log.e("TronX", "Scene Switch Adapter - ${e.message}")
                        }

                    }

                } else {
                    val ss = if (switch.switchType =="B" || switch.switchType == "C" ||switch.switchType == "D") {
                        holder.itemView.seekBar_fan_dimmer.progress
                    }
                    else if (holder.itemView.select_switch.isChecked)  1  else  0
                    addToSeletedSwitch(board.thing_serial_number, board.thing_id, switch.switchId, ss)
                }
                /*addToSeletedSwitch(board.thing_serial_number, board.thing_id, switch.switchId, if (holder.itemView.select_switch.isChecked)  1  else  0)*/
            }
            else {
                if (switch.twoway_id != null) {
                    switch.twoway_id?.let {
                        try {
                            if (twoWayFlag) {
                                twoWayFlag = false
                            } else {
                                twoWay.remove(it.toInt())
                                removeFromSelectedSwitch(board.thing_id, switch.switchId)
                            }
                        }
                        catch (e : Exception) {
                            Log.e ("TronX", "Scene switch adapter - ${e.message}")
                        }
                    }


                } else
                removeFromSelectedSwitch(board.thing_id, switch.switchId)
            }
        }
        if (switch.switchType == "E" || switch.switchType == "F") {
            holder.itemView.select_switch.isEnabled = false
            holder.itemView.scene_switch_checked.isEnabled = false
        }
    }

    fun removeFromSelectedSwitch (thingsID: Int, switchId: Int) {
        selectedBoard.value?.removeAll{
            it.switchId == switchId && it.thing_id == thingsID
        }
    }

    fun addToSeletedSwitch (sno : String, thingsID: Int, switchId: Int, switchStatus : Int) {
        selectedBoard.value?.removeAll{
            it.switchId == switchId && it.thing_id == thingsID
        }
        selectedBoard.value?.add(SceneSwitchDetailModel(thingsID, sno, switchId, switchStatus))
    }

    fun isAddedToScene (thingsID : Int, switchId : Int) : SceneSwitchDetailModel? {
        selectedBoard.value?.forEach {
            if (it.thing_id == thingsID && it.switchId == switchId)
                return it
        }
        return null
    }
}
