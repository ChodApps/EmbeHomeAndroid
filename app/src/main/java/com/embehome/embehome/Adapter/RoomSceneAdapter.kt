package com.embehome.embehome.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.SceneCloudModel
import com.embehome.embehome.R
import com.embehome.embehome.Repository.HubFeatureHandleRepository
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.android.synthetic.main._scene_room_frag.view.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.Adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 28-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RoomSceneAdapter(
    val context: Context,
    val sceneList: MutableLiveData<ArrayList<SceneCloudModel>>?,
    val msg : MutableLiveData<String>
) : RecyclerView.Adapter<RoomSceneAdapter.ViewHolder>() {
    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._scene_room_frag, parent, false))
    }

    override fun getItemCount(): Int {
        val size = sceneList?.value?.size
        if (size != null && size > 0) msg.value = "" else  msg.value = "Scenes not available"
        return size ?: 0//if (size != null && size > 0) size else 5
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (sceneList?.value?.size != null && sceneList.value?.size!! > 0) {
            val scene = CacheSceneTwoWay.getSceneItem(sceneList.value!![position].scenename_id)
            holder.itemView.scene_room_frag_name.text = scene.scene_name

            if (scene.image != null) {
                holder.itemView.room_scene_image.setImageBitmap(scene.image)
            }
            else {
                GlobalScope.launch (Main){
                    val res = AppServices.imageSave(context, scene.scene_image)
                    if (res != null) {
                        scene.image = res
                        holder.itemView.room_scene_image.setImageBitmap(scene.image)
                        notifyItemChanged(position)
                    }
                }
            }

            holder.itemView.scene_room.setOnClickListener{
//                AppServices.toastShort(context, "Playing  Scene ${scene.scene_name}")
                HubFeatureHandleRepository.playScene(it.context,
                    CacheHubData.selectedHubIP, CacheHubData.selectedMacID,
                    sceneList.value!![position].scene_id, sceneList.value!![position].subscene_list ?: ArrayList()
                )
            }
        } else {
            val scene = CacheSceneTwoWay.getSceneItem(position+1)
            holder.itemView.scene_room_frag_name.text = scene.scene_name
            if (scene.image != null) {
                holder.itemView.room_scene_image.setImageBitmap(scene.image)
            }
            else {
                GlobalScope.launch (Main){
                    val res = AppServices.imageSave(context, scene.scene_image)
                    if (res != null) {
                        scene.image = res
                        holder.itemView.room_scene_image.setImageBitmap(scene.image)
                        notifyItemChanged(position)
                    }
                }
            }

            holder.itemView.scene_room.setOnClickListener{
                AppServices.toastShort(context, "Please Create Scene to Execute")

            }
        }
    }
}