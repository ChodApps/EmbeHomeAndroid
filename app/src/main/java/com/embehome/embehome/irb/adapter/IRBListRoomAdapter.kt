package com.embehome.embehome.irb.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Activity.RemoteList
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.android.synthetic.main._expandable_select_items.view.*


/** com.tronx.tt_things_app.irb.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 01-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class IRBListRoomAdapter(
    val context: Context,
    val irbData: List<BoardDetails>,
    val irbRemoteData: MutableLiveData<HashMap<String, ArrayList<RemoteCloudModel>>>,
    val onItemClick : (remote : RemoteCloudModel) -> Unit
) : RecyclerView.Adapter<IRBListRoomAdapter.ViewHolder> () {

    var expandedIndex = -1

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._expandable_select_items, parent, false))
    }

    override fun getItemCount(): Int {
        return irbData.size//irbRemoteData.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.imageView245.visibility = View.INVISIBLE
        holder.itemView.expand_list_item_name.text = irbData[position].thing_name//getIRBName(irbRemoteData.value?.keys?.elementAt(position)!!)

        if (expandedIndex == position) {
            holder.itemView.expanded_rec_list.adapter = IRBRemoteListRoomAdapter(context, irbRemoteData.value!![irbRemoteData.value?.keys?.elementAt(position)!!] ?: ArrayList(), onItemClick)
            holder.itemView.expanding_layout.visibility = View.VISIBLE
        }
        else {
            holder.itemView.expanding_layout.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            /*expandedIndex = if (expandedIndex != position)  position else -1
            notifyDataSetChanged()*/
            val i = Intent(context, RemoteList::class.java)
            i.putExtra("sno",irbData[position].thing_serial_number)
            context.startActivity(i)
        }

    }



    fun getIRBName (sno : String) : String{
        irbData.forEach{
            if (it.thing_serial_number == sno)
                return it.thing_name
        }
        return "New - IRB"
    }
}