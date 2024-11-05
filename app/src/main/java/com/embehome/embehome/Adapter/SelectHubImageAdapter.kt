package com.embehome.embehome.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.HubImageNames
import com.embehome.embehome.R
import com.embehome.embehome.getHubImage
import kotlinx.android.synthetic.main._image_grid_big.view.*


/** com.tronx.tt_things_app.Adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 07-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SelectHubImageAdapter (val context : Context, val name : MutableLiveData<String>) : RecyclerView.Adapter<SelectHubImageAdapter.ViewHolder>() {

    val data = HubImageNames.values()

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout._image_grid_big, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return HubImageNames.values().size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.imageView227.setImageResource(getHubImage(data[position].name))
        holder.itemView.textView167.visibility = View.GONE

        holder.itemView.imageView227.setOnClickListener{
            name.value = data[position].name
        }
    }
}