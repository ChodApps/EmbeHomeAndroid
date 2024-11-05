package com.embehome.embehome.scene.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import kotlinx.android.synthetic.main._select_item_checkox.view.*


/** com.tronx.tt_things_app.scene
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 05-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SceneBoardAdapter (val context: Context, val data: ArrayList<BoardDetails>?, val selectedBoard : MutableLiveData<ArrayList<Int>>) : RecyclerView.Adapter<SceneBoardAdapter.ViewHolder>() {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val root = LayoutInflater.from(context).inflate(R.layout._select_item_checkox, parent, false)

        return ViewHolder(
            root
        )
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board = data!![position]
        holder.itemView.select_checkbox_name.text = board.thing_name
        holder.itemView.select_item_checked.isChecked = selectedBoard.value!!.contains(board.thing_id)
        holder.itemView.select_item_checked.setOnClickListener {
            if (holder.itemView.select_item_checked.isChecked) {
                selectedBoard.value!!.add(board.thing_id)
                Log.d("TronX", "SSB added in List")
            } else {
                selectedBoard.value!!.removeAll{ it == board.thing_id}
                Log.d("TronX", "SSB removed in List")
            }
        }
    }
}
