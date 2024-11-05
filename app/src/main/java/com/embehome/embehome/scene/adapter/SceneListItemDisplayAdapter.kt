package com.embehome.embehome.scene.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.SceneCloudModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.android.synthetic.main._select_item_switch.view.*


/** com.tronx.tt_things_app.scene.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 08-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SceneListItemDisplayAdapter(val context: Context, val data: MutableLiveData<SceneCloudModel>) : RecyclerView.Adapter<SceneListItemDisplayAdapter.ViewHolder> () {

    val switchesSize = data.value?.device_list?.size ?: 0
    val irButtonSize = data.value?.ir_data?.size ?: 0

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder (LayoutInflater.from(context).inflate(R.layout._select_item_switch, parent, false))
    }

    override fun getItemCount(): Int {
        return switchesSize + irButtonSize
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < switchesSize) {
            val item = data.value?.device_list!![position]
            val board = CacheSceneTwoWay.getBoardData(CacheHubData.selectedMacID, item.thing_serial_number)
            if (board != null) {
                val switch = board?.switchesList?.filter { it.switchId == item.switchId }!![0]
                if (switch.switchType == "B" || switch.switchType == "C" ||switch.switchType == "D") {
                    holder.itemView.seekBar_fan_dimmer.visibility = View.VISIBLE
                    holder.itemView.seekBar_fan_dimmer.isEnabled = false
                    holder.itemView.seekBar_fan_dimmer.max = if (switch.switchType == "B") 4 else if (switch.switchType == "C") 10 else if (switch.switchType == "D") 100 else 0
                    holder.itemView.seekBar_fan_dimmer.progress = item.switchstatus
                }else {
                    holder.itemView.seekBar_fan_dimmer.visibility = View.GONE
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

                holder.itemView.select_switch.text = "${board.thing_name} - ${switch.switchName}"
                holder.itemView.select_switch.isChecked = item.switchstatus != 0
                holder.itemView.scene_switch_checked.visibility = View.GONE
                holder.itemView.select_switch_name_sec.visibility = View.GONE
                holder.itemView.select_switch.isEnabled = false
            }
            else {
                holder.itemView.sceneDisplayParentLayout.visibility = View.GONE
            }
        }

        else if (position >= switchesSize) {
            val p = position - switchesSize
//            val item = data.value?.ir_data!![p]
            data.value?.let {
                if (it.ir_data.size > p) {
                    it.ir_data[p]?.let { item ->
                        holder.itemView.select_switch_name_sec.text =
                            "${item.remote_name} - ${item.remote_key}    :    ${item.time_interval} sec"
                        holder.itemView.select_switch_name_sec.visibility = View.VISIBLE
                        holder.itemView.select_switch.visibility = View.GONE
                        holder.itemView.scene_switch_checked.visibility = View.GONE
                    }
                }
            }
            }

        }
    }