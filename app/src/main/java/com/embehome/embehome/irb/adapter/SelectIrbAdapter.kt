package com.embehome.embehome.irb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import kotlinx.android.synthetic.main._list_name_icon.view.*


/** com.tronx.tt_things_app.irb.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 28-05-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SelectIrbAdapter (val context: Context, val data : List<BoardDetails>, val selectIRB : MutableLiveData<BoardDetails>) : RecyclerView.Adapter<SelectIrbAdapter.ViewHolder>() {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._list_name_icon, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.select_list_name.text = data[position].thing_name
        holder.itemView.select_list_icon.visibility = View.GONE
        holder.itemView.setOnClickListener {
            selectIRB.value = data[position]
        }
    }

}