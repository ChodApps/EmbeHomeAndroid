package com.embehome.embehome.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.android.synthetic.main._select_item_checkox.view.*

class TwoWaySelectBoardAdapter(val context: Context, val d : ArrayList<BoardDetails>?, val type: String) : RecyclerView.Adapter<TwoWaySelectBoardAdapter.ViewHolder>() {

    val data = d?.filter {
        it.thing_type != "CUR" && it.thing_type != "IRB"
    }

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val root = LayoutInflater.from(context).inflate(R.layout._select_item_checkox, parent, false)

        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.select_checkbox_name.text = data?.get(position)?.thing_name ?: ""

        if (type == "TwoWay") {
            holder.itemView.select_item_checked.setOnClickListener{
                if(holder.itemView.select_item_checked.isChecked){
                    if (CacheSceneTwoWay.twoWayPid == -1) {
                        CacheSceneTwoWay.twoWayPid = data?.get(position)?.thing_id ?: -1
                        CacheSceneTwoWay.twopsno = data?.get(position)?.thing_serial_number ?: ""
                    }
                    else {
                        CacheSceneTwoWay.twoWaySid = data?.get(position)?.thing_id ?: -1
                        CacheSceneTwoWay.twossno = data?.get(position)?.thing_serial_number ?: ""
                    }
                }
                else {
                    if (CacheSceneTwoWay.twoWayPid == data?.get(position)?.thing_id) {
                        CacheSceneTwoWay.twoWayPid = CacheSceneTwoWay.twoWaySid
                        CacheSceneTwoWay.twopsno = CacheSceneTwoWay.twossno
                        CacheSceneTwoWay.twoWaySid = -1
                    }
                    else {
                        CacheSceneTwoWay.twoWaySid = -1
                    }
                }
                notifyDataSetChanged()
            }
            Log.d("TronX", "${CacheSceneTwoWay.twoWayPid} ${CacheSceneTwoWay.twoWaySid}")
            holder.itemView.select_item_checked.isEnabled = CacheSceneTwoWay.twoWayPid == -1 || CacheSceneTwoWay.twoWaySid == -1
            if (CacheSceneTwoWay.twoWayPid == data?.get(position)?.thing_id || CacheSceneTwoWay.twoWaySid == data?.get(position)?.thing_id) {
                holder.itemView.select_item_checked.isChecked = true
                holder.itemView.select_item_checked.isEnabled = true
            }
        }
        else if (type == "Scene") {
            holder.itemView.select_item_checked.isChecked = CacheSceneTwoWay.sceneCreateSSBData.contains(data?.get(position)!!.thing_id)
            holder.itemView.select_item_checked.setOnClickListener{
                if (holder.itemView.select_item_checked.isChecked) {
                    CacheSceneTwoWay.sceneCreateSSBData.add(data.get(position).thing_id)
                    Log.d("TronX", "SSB added in List")
                }
                else {
                    CacheSceneTwoWay.sceneCreateSSBData.removeAll{ it == data.get(position).thing_id }
                    Log.d("TronX", "SSB removed in List ${CacheSceneTwoWay.sceneCreateSSBData}")
                }
            }
        }
    }
}