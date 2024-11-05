package com.embehome.embehome.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.SceneSwitchDetailModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.android.synthetic.main._select_item_switch.view.*

class SceneSelectSwitchAdapter(val context: Context, val data: ArrayList<BoardDetails>) : RecyclerView.Adapter<SceneSelectSwitchAdapter.ViewHolder>() {

    val size = getListSize()


    private fun getListSize (): Int {
        var size = 0
        data.forEach {
            size += it.switchesList.size
        }
        return size
    }

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val root = LayoutInflater.from(context).inflate(R.layout._select_item_switch, parent, false)

        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        return size
    }

    fun getBoardTitle (areaName : String, boardName :String): String {
        return "$areaName - $boardName"
    }

    fun getStatusOFSwitch (tid : Int, sid : Int): Boolean {
        CacheSceneTwoWay.sceneCreateSwitchData.forEach {
            if (it.thing_id == tid && it.switchId == sid)
                return true
        }
        return false
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       var tSize = 0
        data.forEach {
            if (position < tSize + it.switchesList.size) {
                val index = position - tSize
                if (index == 0) {
                    holder.itemView.switch_title_optional.text = getBoardTitle(CacheHubData.getArea(it.area_id).area_name, it.thing_name)
                    holder.itemView.switch_title_optional.visibility = View.VISIBLE
                }
                else {
                    holder.itemView.switch_title_optional.visibility = View.GONE
                }
                holder.itemView.select_switch.text = it.switchesList[index].switchName
                holder.itemView.select_switch.isChecked = getStatusOFSwitch(it.thing_id, it.switchesList[index].switchId)
                return@forEach
            }
            else {
                tSize += it.switchesList.size
            }
        }
        holder.itemView.select_switch.setOnClickListener{
            var tempSize = 0
            data.forEach {
                if (position < tempSize + it.switchesList.size) {
                    val index = position - tempSize
                    if (holder.itemView.select_switch.isChecked) {
                        addToSceneData(it.thing_id, it.thing_serial_number, it.switchesList[index].switchId)
                        return@setOnClickListener
                    }
                    else {
                        removeFromSceneData(it.thing_id, it.switchesList[index].switchId)
                    }
                }
                else {
                    tempSize += it.switchesList.size
                }
            }
        }
    }

    private fun addToSceneData (thingsID : Int,thingsSno : String,  switchId : Int) {
        CacheSceneTwoWay.sceneCreateSwitchData.add(SceneSwitchDetailModel(thingsID, thingsSno, switchId, 1))
        Log.d("TronX", "Scene SwitchData ${CacheSceneTwoWay.sceneCreateSwitchData}")
    }

    private fun removeFromSceneData (thingsID: Int, switchId: Int) {
        CacheSceneTwoWay.sceneCreateSwitchData.removeAll{
            it.thing_id == thingsID && it.switchId == switchId
        }
    }
}