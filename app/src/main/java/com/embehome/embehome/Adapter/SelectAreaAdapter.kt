package com.embehome.embehome.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Activity.FragmentHandler
import com.embehome.embehome.Model.AreaData
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.android.synthetic.main._image_grid_big.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.Adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 02-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SelectAreaAdapter (val context: Context, val areaSelected : MutableLiveData<AreaData>) : RecyclerView.Adapter<SelectAreaAdapter.ViewHolder>(){

    val areaList = CacheHubData.getAreaList()

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data : AreaData, pos : Int) {
            itemView.textView167.text = data.area_name
            itemView.setOnClickListener{
                Log.d("TronX","You have selected ${data.area_name}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._image_grid_big, parent, false))
    }

    override fun getItemCount(): Int {
        return  areaList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        areaList?.let {
            val area = it[position]
            holder.bind(area, position)
            holder.itemView.setOnClickListener{
                //Log.d("TronX","You have selected ${data["area_name"]}")
                areaSelected.value = area
            }

            if (area.default) {
                holder.itemView.cardView22.visibility = View.GONE
            }
            else {
                holder.itemView.cardView22.visibility = View.VISIBLE
            }

            holder.itemView.cardView22.setOnClickListener {
                val intent = Intent(context, FragmentHandler::class.java)
                intent.putExtra("fragment", "editArea")
                intent.putExtra("id", area.area_id)
                context.startActivity(intent)
            }


            try {
                if (area.image != null)
                    holder.itemView.imageView227.setImageBitmap(area.image)
                else {
                    holder.itemView.imageView227.setImageResource(R.drawable.place_holder_2)
                    GlobalScope.launch (Dispatchers.Main) {
                        val res = AppServices.imageSave(context, area.area_image)
                        if (res != null){
                            area.image = res
                            /*holder.itemView.imageView227.setImageBitmap(area.image)
                            notifyItemChanged(position)*/
                            notifyDataSetChanged()
                        }
                    }
                }
            }
            catch (e :Exception) {
                AppServices.log (e.message.toString())
                AppServices.log("TronX _ TCP" , e.stackTrace.toList().toString())
            }
        }
//        val area = CacheHubData.getArea(position + 1)
//        holder.bind(FakeDB.areaData[position], position)
//        holder.itemView.setOnClickListener{
//            //Log.d("TronX","You have selected ${data["area_name"]}")
//            areaSelected.value = FakeDB.areaData[position]
//        }
//
//        try {
//            if (area.image != null)
//                holder.itemView.area_image.setImageBitmap(area.image)
//            else {
//                GlobalScope.launch (Dispatchers.Main) {
//                    val res = AppServices.imageSave(context, area.area_image)
//                    if (res != null){
//                        area.image = res
//                        holder.itemView.area_image.setImageBitmap(area.image)
//                        notifyItemChanged(position)
//                    }
//                }
//            }
//        }
//        catch (e :Exception) {
//            AppServices.log (e.message.toString())
//            AppServices.log("TronX _ TCP" , e.stackTrace.toList().toString())
//        }
    }
}