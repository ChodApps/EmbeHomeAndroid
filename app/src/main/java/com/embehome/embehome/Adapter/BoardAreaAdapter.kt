package com.embehome.embehome.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Manager.SSBManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import kotlinx.android.synthetic.main._individual_board.view.*


/** com.tronx.tt_things_app.Adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 28-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class BoardAreaAdapter (val context : Context, val data : ArrayList<BoardDetails>, val selectedBoard : MutableLiveData<Int>) : RecyclerView.Adapter<BoardAreaAdapter.ViewHolder>() {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout._individual_board, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.individual_board_name.text = data[position].thing_name
        if (position == selectedBoard.value) {
            holder.itemView.image_grid_image.setImageResource(SSBManager.getSSBImage(data[position].switchesList.size, true, data[position].thing_type))
        }
        else {
            holder.itemView.image_grid_image.setImageResource(SSBManager.getSSBImage(data[position].switchesList.size, false, data[position].thing_type))
        }
        holder.itemView.setOnClickListener {
            selectedBoard.value = position
        }
    }
}