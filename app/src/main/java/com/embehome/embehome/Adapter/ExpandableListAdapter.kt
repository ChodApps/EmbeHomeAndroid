package com.embehome.embehome.Adapter

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

class ExpandableListAdapter (val context : Context, val reqFrom : String = "") : RecyclerView.Adapter<ExpandableListAdapter.ViewHolder>() {

    var expandedIndex = -1
    var list = CacheHubData.getHubData(CacheHubData.selectedMacID)
    val data = ArrayList<Int>().apply {
        list . value ?. keys ?. forEach {id ->
            val r = list.value!![id]?.value?.filter {
                it.thing_type != "IRB" && it.thing_type != "CUR"
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



        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.expand_list_item_name.text = CacheHubData.getArea(data.elementAt(position) ?: 0).area_name
        if (expandedIndex == position) {
            holder.itemView.expanding_layout.visibility = View.VISIBLE

            val b = getBoardData(data.elementAt(position))
            b?.value?.let {
                holder.itemView.expanded_rec_list.adapter = if (reqFrom != "TwoWay") TwoWaySelectBoardAdapter(context, it, reqFrom)
                else TwoWaySelectBoardAdapter(context, it, "TwoWay")
            }
//            holder.itemView.expanded_rec_list.adapter = if (reqFrom != "TwoWay") TwoWaySelectBoardAdapter(context, data.get(data.elementAt(position))?.value, reqFrom)
//            else TwoWaySelectBoardAdapter(context, data.value?.get(data.value?.keys?.elementAt(position))?.value, "TwoWay")
        }
        else {
            holder.itemView.expanding_layout.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            expandedIndex = if (expandedIndex != position)  position else -1
            notifyDataSetChanged()
        }
    }

    private fun getBoardData (id : Int): MutableLiveData<ArrayList<BoardDetails>>? {
        return list.value?.get(id)
    }
}