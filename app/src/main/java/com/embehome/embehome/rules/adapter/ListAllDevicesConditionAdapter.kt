package com.embehome.embehome.rules.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import kotlinx.android.synthetic.main._expandable_select_items.view.*


/** com.tronx.tt_things_app.rules.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 23-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class ListAllDevicesConditionAdapter(val context : Context,
                                     val expanded : MutableLiveData<Int>,
                                     val boardData : ArrayList<BoardDetails>,
                                     val selectedSwitchId : MutableLiveData<Int>,
                                     val selectedBoardID : MutableLiveData<Int>,
                                     val selectedSwitchStatus: MutableLiveData<Int>,
                                     val serialNo : MutableLiveData<String>
) : RecyclerView.Adapter<ListAllDevicesConditionAdapter.ViewHolder>() {

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
                RuleConditionSwitchAdapter(
                    context,
                    boardData[position],
                    selectedBoardID,
                    selectedSwitchId,
                    selectedSwitchStatus,
                    serialNo
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