package com.embehome.embehome.irb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.R
import com.embehome.embehome.irb.RemoteCategoryEnum
import com.embehome.embehome.irb.getResourceRemoteIcon
import kotlinx.android.synthetic.main._remote_category.view.*


/** com.tronx.tt_things_app.irb.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 22-05-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RemoteCategoryAdapter(
    val context: Context,
    val data: List<RemoteCategoryEnum>,
    val irbCategory: MutableLiveData<RemoteCategoryEnum>
) : RecyclerView.Adapter <RemoteCategoryAdapter.ViewHolder> () {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._remote_category, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (irbCategory.value != null && irbCategory.value == data[position]) {
            holder.itemView.remote_category_select.visibility = View.VISIBLE
        }
        else {
            holder.itemView.remote_category_select.visibility = View.GONE
        }
        holder.itemView.remoteCategoryIcon.setImageResource(getResourceRemoteIcon(data[position].name))
        holder.itemView.remoteCategoryName.text = data[position].name.replace("_"," ")
        holder.itemView.setOnClickListener {
            irbCategory.value = data[position]
        }
    }
}