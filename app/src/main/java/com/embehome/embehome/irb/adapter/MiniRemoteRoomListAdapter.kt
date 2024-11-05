package com.embehome.embehome.irb.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.R
import com.embehome.embehome.irb.getResourceRemoteIcon
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.android.synthetic.main._individual_switch.view.*


/** com.tronx.tt_things_app.irb.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 03-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class MiniRemoteRoomListAdapter (val context : Context, val remotes : MutableLiveData<ArrayList<RemoteCloudModel>>, val selectedRemote : MutableLiveData<RemoteCloudModel>, val onRemoteClick : (remote : RemoteCloudModel) -> Unit) : RecyclerView.Adapter<MiniRemoteRoomListAdapter.ViewHolder> () {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._individual_switch, parent, false))
    }

    override fun getItemCount(): Int {
        return remotes.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onRemoteClick(remotes.value?.get(position)!!)
            selectedRemote.value = remotes.value?.get(position)!!
            notifyDataSetChanged()
        }

        selectedRemote.value?.let {
            if (remotes.value!![position].remote_id == it.remote_id) {
                holder.itemView.switchSelectOutline1.setBackgroundColor(context.getColor(R.color.colorAccent))
                holder.itemView.switchSelectOutline2.setBackgroundColor(context.getColor(R.color.colorAccent))
                holder.itemView.switchSelectOutline3.setBackgroundColor(context.getColor(R.color.colorAccent))
                holder.itemView.switchSelectOutline4.setBackgroundColor(context.getColor(R.color.colorAccent))
                holder.itemView.imageView274.visibility = View.VISIBLE
            } else {
                holder.itemView.switchSelectOutline1.setBackgroundColor(Color.TRANSPARENT)
                holder.itemView.switchSelectOutline2.setBackgroundColor(Color.TRANSPARENT)
                holder.itemView.switchSelectOutline3.setBackgroundColor(Color.TRANSPARENT)
                holder.itemView.switchSelectOutline4.setBackgroundColor(Color.TRANSPARENT)
                holder.itemView.imageView274.visibility = View.INVISIBLE
            }
        }

        holder.itemView.individual_switch_name.text = getName(remotes.value!![position].remote_name)
        val res = getResourceRemoteIcon(remotes.value!![position].ir_category)
        holder.itemView.image_grid_image.setImageResource(res)
    }

    fun getName (name : String) : String {
        return if (name.length >= 14)
            name
        else {
            val size = (14 - name.length) / 2
            "${" ".repeat(size)}$name${" ".repeat(size)}"
        }
    }


}