package com.embehome.embehome.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Activity.AreaOperation
import com.embehome.embehome.Model.AreaData
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.android.synthetic.main._area_list.view.area_image
import kotlinx.android.synthetic.main._area_list.view.area_name
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.Adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 26-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class AreaAdapter (val context : Context, val data : MutableLiveData<HashMap<Int, MutableLiveData<ArrayList<BoardDetails>>>>, val gridView : Boolean) : RecyclerView.Adapter<AreaAdapter.ViewHolder>() {
    init {
        data.value?.let {d ->
            d.keys.filter {
                it <= 0
            }.also {
                it.forEach{
                    d.remove(it)
                }
            }
        }
    }


    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {



        fun bind(data : Int, pos : Int) {
            var temp = AreaData(0,"","",false)

//            FakeDB.areaData.forEach {
//             if (it.area_id == data) {
//                 temp = it
//             }
//            }
            temp = CacheHubData.getArea(data)




            itemView.area_name.text = temp.area_name

            itemView.area_name.setOnClickListener {
                if (temp.area_id >= 0) {
                    CacheHubData.selectedAreaId = data
                    it.context.startActivity(Intent(it.context.applicationContext, AreaOperation::class.java))
                }
            }

            itemView.setOnClickListener{
//                it.findNavController().navigate(RoomFragmentDirections.actionRoomFragment2ToAreaFragment("$data"))
                if (temp.area_id >= 0) {
                    CacheHubData.selectedAreaId = data
                    it.context.startActivity(Intent(it.context.applicationContext, AreaOperation::class.java))
                }
//                val d = RoomFragmentDirections.actionRoomFragment2ToDeviceArea(areaId = data, macId = CacheHubData.selectedMacID)
//                it.findNavController().navigate(d)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (gridView) ViewHolder(LayoutInflater.from(context).inflate(R.layout._area_grid, parent, false))
            else ViewHolder(LayoutInflater.from(context).inflate(R.layout._area_list, parent, false))
    }

    override fun getItemCount(): Int {
        return data.value?.keys?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data.value?.keys!!.elementAt(position)
        if (item == 0) {
            CacheHubData.removeArea(CacheHubData.selectedMacID, item)

        }
        holder.bind(item, position)
        val area = CacheHubData.getArea(item)
        try {

            if (area.image != null)
                holder.itemView.area_image.setImageBitmap(area.image)
            else {
                GlobalScope.launch (Main) {
                    val res = AppServices.imageSave(context, area.area_image)
                    if (res != null){
                        area.image = res
//                        holder.itemView.area_image.setImageBitmap(area.image)
//                        notifyItemChanged(position)
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

}