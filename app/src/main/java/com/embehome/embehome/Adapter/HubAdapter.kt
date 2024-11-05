package com.embehome.embehome.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Activity.HubActivity
import com.embehome.embehome.Model.HubData
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.getHubImage
import kotlinx.android.synthetic.main._hub.view.*


/** com.tronx.tt_things_app.Adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 24-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class HubAdapter(val context: Context, val data: MutableLiveData<ArrayList<HubData>?>) : RecyclerView.Adapter<HubAdapter.viewHolder>() {
    class viewHolder( binding : View) : RecyclerView.ViewHolder(binding) {

        @ExperimentalStdlibApi
        fun bind(pos : HubData) {
            itemView.home_hub_name.text = pos.name
            itemView.temp_hub_name.setOnClickListener{
                Log.d("TronX","Clicked Hub list ")
                FakeDB.macID = pos.macID
                CacheHubData.selectedMacID = pos.macID
//                CacheHubData.getAllIpConnectedInNetwork()
                CacheHubData.getIp(CacheHubData.selectedMacID).value.let {ip ->
                    CacheHubData.selectedHubIP = ip ?: ""
                    if (ip != null && ip.isEmpty()) {

                    }
                }
                it.context.startActivity(Intent(it.context, HubActivity::class.java))
            }
            if (pos.image.isNotEmpty()) itemView.home_hub_image.setImageResource(getHubImage(pos.image))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout._hub, parent, false)
        return viewHolder(v)
    }

    override fun getItemCount(): Int {
        return data!!.value?.size ?: 0
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(data!!.value!![position])
    }
}