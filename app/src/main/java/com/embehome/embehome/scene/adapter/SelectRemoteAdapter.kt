package com.embehome.embehome.scene.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.R
import com.embehome.embehome.irb.getResourceRemoteIcon
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.android.synthetic.main._list_name_icon.view.*


/** com.tronx.tt_things_app.scene.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 23-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SelectRemoteAdapter (val context: Context, val data : List<RemoteCloudModel>, val selectIRB : MutableLiveData<RemoteCloudModel>) : RecyclerView.Adapter<SelectRemoteAdapter.ViewHolder>() {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._list_name_icon, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.select_list_name.text = data[position].remote_name
        holder.itemView.select_list_icon.setImageResource(getResourceRemoteIcon(data[position].ir_category))
        holder.itemView.setOnClickListener {
            selectIRB.value = data[position]
        }
    }

}