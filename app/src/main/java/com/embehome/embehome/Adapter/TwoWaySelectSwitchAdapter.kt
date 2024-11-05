package com.embehome.embehome.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.android.synthetic.main._select_item_checkox.view.*

class TwoWaySelectSwitchAdapter (val context : Context, val data : ArrayList<BoardDetails>) : RecyclerView.Adapter<TwoWaySelectSwitchAdapter.ViewHolder>() {

    private val priBoardData = data[0]
    private val secBoardData = data[1]

    private val priSwitch = (priBoardData.switchesList.size )
    private val secSwitch = (secBoardData.switchesList.size )

    val twoWayData = CacheSceneTwoWay.getTwoWayData(CacheHubData.selectedMacID)

    val twoWayPri = twoWayData?.value?.filter {
        it.device_details.pri_thing_id == CacheSceneTwoWay.twoWayPid || it.device_details.sec_thing_id == CacheSceneTwoWay.twoWayPid
    }

    val twoWaySec = twoWayData?.value?.filter {
        it.device_details.pri_thing_id == CacheSceneTwoWay.twoWaySid || it.device_details.sec_thing_id == CacheSceneTwoWay.twoWaySid
    }



    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val root = LayoutInflater.from(context).inflate(R.layout._select_item_checkox, parent, false)

        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        return (priSwitch + secSwitch)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < priSwitch) {
            if (position == 0) {
                holder.itemView.switch_title_optional.visibility = View.VISIBLE
                holder.itemView.switch_title_optional.text = getRoomBoardTitle(priBoardData.thing_id, priBoardData.thing_name)
            }
            else {
                holder.itemView.switch_title_optional.visibility = View.GONE
            }
            val switch = priBoardData.switchesList[position]
            holder.itemView.select_checkbox_name.text = priBoardData.switchesList[position].switchName
            holder.itemView.select_item_checked.isEnabled = switch.switchType != "F" && switch.switchType != "E" && switch.twoway_id == null && (CacheSceneTwoWay.twoWayPsid == -1 || CacheSceneTwoWay.twoWayPsid == priBoardData.switchesList[position].switchId)
            holder.itemView.select_item_checked.isChecked = CacheSceneTwoWay.twoWayPsid != -1 && CacheSceneTwoWay.twoWayPsid == priBoardData?.switchesList!![position].switchId
//            holder.itemView.select_item_checked.isEnabled = isTwoWay(CacheSceneTwoWay.twoWayPid, CacheSceneTwoWay.twoWayPsid)
        }
        else {
            if ((position - priSwitch ) == 0) {
                holder.itemView.switch_title_optional.visibility = View.VISIBLE
                holder.itemView.switch_title_optional.text = getRoomBoardTitle(secBoardData?.thing_id!!, secBoardData.thing_name)
            }
            else {
                holder.itemView.switch_title_optional.visibility = View.GONE
            }
            holder.itemView.select_item_checked.isChecked = CacheSceneTwoWay.twoWaySsid != -1 && CacheSceneTwoWay.twoWaySsid == secBoardData?.switchesList!![position - priSwitch].switchId
            holder.itemView.select_checkbox_name.text = secBoardData?.switchesList!![position - priSwitch].switchName
            val s = secBoardData?.switchesList!![position - priSwitch]
            holder.itemView.select_item_checked.isEnabled  = s.switchType != "F" && s.switchType != "E" && s.twoway_id == null && (CacheSceneTwoWay.twoWaySsid == -1 || CacheSceneTwoWay.twoWaySsid == secBoardData?.switchesList!![position - priSwitch].switchId)
//            holder.itemView.select_item_checked.isEnabled = isTwoWay(CacheSceneTwoWay.twoWaySid, CacheSceneTwoWay.twoWaySsid)

        }

        holder.itemView.select_item_checked.setOnClickListener{
            if (position < priSwitch) {
                if(holder.itemView.select_item_checked.isChecked) {
                    CacheSceneTwoWay.twoWayPsid = priBoardData.switchesList!![position].switchId
                }
                else {
                    CacheSceneTwoWay.twoWayPsid  = -1
                }
            }
            else {
                if(holder.itemView.select_item_checked.isChecked) {
                    CacheSceneTwoWay.twoWaySsid = secBoardData?.switchesList!![position - priSwitch].switchId
                }
                else {
                    CacheSceneTwoWay.twoWaySsid  = -1
                }
            }
            notifyDataSetChanged()
        }
    }

    fun getRoomBoardTitle (thingsId: Int, boardName : String): String {
        val areaName = CacheHubData.getAreaName(thingsId)
        return "$areaName - $boardName"
    }


}