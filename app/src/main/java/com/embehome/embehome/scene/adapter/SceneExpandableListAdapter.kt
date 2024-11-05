package com.embehome.embehome.scene.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.android.synthetic.main._expandable_select_items.view.*


/** com.tronx.tt_things_app.scene
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 05-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SceneExpandableListAdapter (
    val context : Context,
    val expanded : MutableLiveData<Int>,
    val boardData : MutableLiveData<HashMap<Int, MutableLiveData<ArrayList<BoardDetails>>>>,
    val selectedBoard : MutableLiveData<ArrayList<Int>>,
    val action : () -> Unit
) : RecyclerView.Adapter<SceneExpandableListAdapter.ViewHolder>() {

    val list = ArrayList<Int>().apply {
        boardData . value ?. keys ?. forEach {id ->
        val r = boardData.value!![id]?.value?.filter {
            it.thing_type != "IRB"
        }
            if (r != null && r.size  > 0) {
                add(id)
            }
        }
    }
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout._expandable_select_items, parent, false)
        return ViewHolder(
            v
        )
    }

    override fun getItemCount(): Int {
        return list.size//boardData.value?.keys?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val area =  CacheHubData.getArea(list[position])//CacheHubData.getArea(boardData.value?.keys?.elementAt(position) ?: 0)
//        boardData.value?.get(area.area_id)?.let {
//            it.value?.let {
//                it.filter {
//                    it.thing_type != "IRB"
//                }.also {
//                    if (it.size <=  0)
//                        holder.itemView.visibility = View.GONE
//
//                }
//            }
//        }
        holder.itemView.expand_list_item_name.text = area.area_name
        if (expanded.value == position) {
            holder.itemView.expanding_layout.visibility = View.VISIBLE
            val boards = ArrayList<BoardDetails>()
            boardData.value!![list[position]]?.value.also {
                it?.forEach {
                   if (it.thing_type == "SSB" || it.thing_type == "CUR" || it.thing_type == "ISB" || it.thing_type == "RSB")
                       boards.add(it)
                }
            }
            holder.itemView.expanded_rec_list.adapter =
                SceneBoardAdapter(
                    context,
                    boards,
                    selectedBoard
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