package com.embehome.embehome.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Fragments.SceneDetailDirections
import com.embehome.embehome.Model.SceneItemModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import kotlinx.android.synthetic.main._image_list_big.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class selectSceneItemAdapter (val context: Context, val data : ArrayList<SceneItemModel>, val action : (id : SceneItemModel) -> Unit) : RecyclerView.Adapter<selectSceneItemAdapter.ViewHolder> () {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._image_list_big, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scene = data[position]
        holder.itemView.textView167.text = scene.scene_name
        if (scene.image != null) {
            holder.itemView.imageView227.setImageBitmap(scene.image)
        }
        else {
            holder.itemView.imageView227.setImageResource(R.drawable.place_holder_2)
            GlobalScope.launch (Dispatchers.Main) {
                val res = AppServices.imageSave(context, scene.scene_image)
                if (res != null && scene.image == null) {
                    scene.image = res
                    /*holder.itemView.imageView227.setImageBitmap(scene.image)
                    notifyItemChanged(position)*/
                    notifyDataSetChanged()
                }
            }
        }

        if (scene.default) {
            holder.itemView.cardView22.visibility = View.GONE
        }
        else {
            holder.itemView.cardView22.visibility = View.VISIBLE
        }
        holder.itemView.cardView22.setOnClickListener {
            val d = SceneDetailDirections.actionSceneDetailToCreateArea3("Scene","Update", scene.scenename_id)
            it.findNavController().navigate(d)
        }

        holder.itemView.setOnClickListener {
            action(data[position])
        }
    }
}