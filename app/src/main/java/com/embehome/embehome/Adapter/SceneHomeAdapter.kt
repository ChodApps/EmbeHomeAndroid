package com.embehome.embehome.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.SceneCloudModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.android.synthetic.main._room_scene.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SceneHomeAdapter (val context : Context,val data : MutableLiveData<ArrayList<SceneCloudModel>>, val onCLick : (view : View, position : Int) -> Unit, val detail : (sceneID : Int) -> Unit) : RecyclerView.Adapter<SceneHomeAdapter.viewHolder> (){
    class viewHolder (itemView : View): RecyclerView.ViewHolder (itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {

        return viewHolder(LayoutInflater.from(context).inflate(R.layout._room_scene, parent, false))

    }

    override fun getItemCount(): Int {
        return data.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.room_scene_play_btn.setOnClickListener {
            it.startAnimation(AlphaAnimation(1F, 0.6F))
            onCLick(it, position)
        }

        holder.itemView.setOnClickListener{
            detail(data.value!![position].scene_id)
        }

        val scene = CacheSceneTwoWay.getSceneItem(data.value!![position].scenename_id)
        holder.itemView.room_scene_display_name.text = scene.scene_name
        if (scene.image != null) {
            holder.itemView.imageView26.setImageBitmap(scene.image)
        }
        else {
            GlobalScope.launch (Dispatchers.Main){
                val res = AppServices.imageSave(context, scene.scene_image)
                if (res != null) {
                    scene.image = res
                    holder.itemView.imageView26.setImageBitmap(scene.image)
                    notifyItemChanged(position)
                }
            }
        }

    }
}