package com.embehome.embehome.scene.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.SceneSwitchDetailModel
import com.embehome.embehome.R
import kotlinx.android.synthetic.main._expandable_select_items.view.*


/** com.tronx.tt_things_app.scene
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 05-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SceneBoardExpandListAdapter(val context : Context,
                                  val expanded : MutableLiveData<Int>,
                                  val boardData : ArrayList<BoardDetails>,
                                  val selectedSwitches : MutableLiveData<ArrayList<SceneSwitchDetailModel>>,
                                  val twoWay : ArrayList<Int>,
                                  val action : () -> Unit
) : RecyclerView.Adapter<SceneBoardExpandListAdapter.ViewHolder>() {

//    private val twoWay = ArrayList<Int> ()

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout._expandable_select_items, parent, false)
        return ViewHolder(
            v
        )
    }

    override fun getItemCount(): Int {
        return boardData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.expand_list_item_name.text = boardData[position].thing_name
        if (expanded.value == position) {
            holder.itemView.expanding_layout.visibility = View.VISIBLE

            holder.itemView.expanded_rec_list.adapter =
                SceneSwitchAdapter(
                    context,
                    boardData[position],
                    selectedSwitches,
                    twoWay
                )
        }
        else {
            holder.itemView.expanding_layout.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            expanded.value = if (expanded.value != position)  position else -1
            notifyDataSetChanged()
        }
    }
}