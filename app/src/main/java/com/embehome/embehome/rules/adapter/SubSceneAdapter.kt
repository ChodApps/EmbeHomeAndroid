package com.embehome.embehome.rules.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.SceneCloudModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.android.synthetic.main._sub_scene_select.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.rules.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 24-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SubSceneAdapter (val context : Context, val data : ArrayList<SceneCloudModel>, val selected : MutableLiveData<ArrayList<Int>>) : RecyclerView.Adapter<SubSceneAdapter.ViewHolder>() {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._sub_scene_select, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scene = data[position]
        val sceneItem = CacheSceneTwoWay.getSceneItem(scene.scenename_id)
        holder.itemView.textView193.text = sceneItem.scene_name
        selected.value?.let {
            holder.itemView.checkBox5.isChecked = it.contains(scene.scene_id)
        }
        if (sceneItem.image != null) {
            holder.itemView.imageView252.setImageBitmap(sceneItem.image)
        }
        else {
            GlobalScope.launch (Dispatchers.Main){
                val res = AppServices.imageSave(context, sceneItem.scene_image)
                if (res != null) {
                    sceneItem.image = res
                    holder.itemView.imageView252.setImageBitmap(sceneItem.image)
                    notifyItemChanged(position)
                }
            }
        }

        holder.itemView.checkBox5.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                addScene(scene.scene_id)
            }
            else {
                removeScene(scene.scene_id)
            }
        }
    }

    private fun addScene (sceneId : Int) {
        selected.value?.let {
            it.add(sceneId)
        }
    }

    private fun removeScene (sceneId : Int) {
        selected.value?.let {
            it.remove(sceneId)
        }
    }
 }