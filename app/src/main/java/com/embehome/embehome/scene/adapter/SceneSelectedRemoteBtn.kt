package com.embehome.embehome.scene.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.scene.IRSceneModel
import kotlinx.android.synthetic.main._irb_scene_item.view.*


/** com.tronx.tt_things_app.scene.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 26-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SceneSelectedRemoteBtn (val context: Context, val data : MutableLiveData<ArrayList<IRSceneModel>>) : RecyclerView.Adapter<SceneSelectedRemoteBtn.ViewHolder>() {


    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._irb_scene_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.irb_scene_name.text = data.value!![position].remote_key.drop(4).replace('_',' ')
        holder.itemView.irb_scene_time.text = data.value!![position].time_interval.toString()
        holder.itemView.irb_scene_remove.setOnClickListener {
            try {
                if (data.value!!.size > 1) {
                    data.value!!.removeAt(position)
                    notifyItemRemoved(position)
                }
                else {
                    if (position == 1)
                        data.value!!.removeAt(position - 1)
                    else
                        data.value!!.removeAt(position)
                    notifyItemRemoved(position)
                }
                data.value = data.value
            }
            catch (e : Exception) {
                AppServices.log(e.message.toString())
            }
            notifyDataSetChanged()
        }
    }


}