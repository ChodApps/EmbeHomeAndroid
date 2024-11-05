package com.embehome.embehome.irb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.R
import com.embehome.embehome.irb.getResourceRemoteIcon
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.android.synthetic.main._list_name_icon.view.*
import java.util.*


/** com.tronx.tt_things_app.irb.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 01-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class IRBRemoteListRoomAdapter(
    val context: Context,
    val remotes: ArrayList<RemoteCloudModel>?,
    val onItemClick : (remote : RemoteCloudModel) -> Unit
) : RecyclerView.Adapter<IRBRemoteListRoomAdapter.ViewHolder> () {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._list_name_icon, parent, false))
    }

    override fun getItemCount(): Int {
        return remotes?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = remotes?.get(position)
        r?.ir_category?.let {
            holder.itemView.select_list_icon.setImageResource(getResourceRemoteIcon(it))
        }
        holder.itemView.select_list_name.text = r?.remote_name
        holder.itemView.setOnClickListener {
            onItemClick(r!!)
        }
    }

}