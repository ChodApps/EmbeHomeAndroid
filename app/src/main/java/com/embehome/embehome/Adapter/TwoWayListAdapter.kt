package com.embehome.embehome.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Manager.SSBManager
import com.embehome.embehome.Model.TwoWayItemModel
import com.embehome.embehome.R
import com.embehome.embehome.Repository.HubFeatureHandleRepository
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.android.synthetic.main._two_way_list_item.view.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TwoWayListAdapter(val context: Context, val data : ArrayList<TwoWayItemModel>) : RecyclerView.Adapter<TwoWayListAdapter.ViewHolder>() {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val root = LayoutInflater.from(context).inflate(R.layout._two_way_list_item, parent, false)


        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getDisplayTitleText (pri :String, post : String) = "$pri - $post"

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val priBoard = CacheSceneTwoWay.getBoardData(
                CacheHubData.selectedMacID,
                data[position].device_details.pri_thing_id
            )
            val secBoard = CacheSceneTwoWay.getBoardData(
                CacheHubData.selectedMacID,
                data[position].device_details.sec_thing_id
            )
            if (priBoard != null && secBoard != null) {
                holder.itemView.two_way_ssb_name.text = priBoard.thing_name
                holder.itemView.two_way_ssb_name2.text = secBoard.thing_name
                holder.itemView.two_way_list_switch_name.text =
                    priBoard.switchesList[data[position].device_details.pri_switchId - 1].switchName
                holder.itemView.imageView246.setImageResource(
                    SSBManager.getSSBImage(
                        priBoard.switchesList.size,
                        true,
                        "SSB"
                    )
                )
                holder.itemView.imageView247.setImageResource(
                    SSBManager.getSSBImage(
                        secBoard.switchesList.size,
                        true,
                        "SSB"
                    )
                )
                holder.itemView.two_way_list_switch_name2.text =
                    secBoard.switchesList[data[position].device_details.sec_switchId - 1].switchName

            }

        }
        catch (e : Exception) {
            Log.e ("TronX_Adapter", "TwowayListAdapter ${e.message}")
        }

            holder.itemView.two_way_delete.setOnClickListener {
                if (CacheHubData.selectedHubIP.length > 5)
                    AppDialogs.openTitleDialog(
                        it.context,
                        "Delete Two way",
                        "Are you sure you want to delete two way",
                        "NO",
                        "YES"
                    ) { dialog, which ->
                        GlobalScope.launch(Main) {
                            AppDialogs.startLoadScreen(it.context)
                            val res = HubFeatureHandleRepository.deleteTwoWay(
                                it.context,
                                CacheHubData.selectedHubIP,
                                CacheHubData.selectedMacID,
                                data[position]
                            )
                            if (res) notifyDataSetChanged()
                            AppDialogs.stopLoadScreen()
                        }
                    }
                else AppServices.toastShort(it.context, CacheHubData.homeNetwork)
            }

            holder.itemView.setOnClickListener {
            }

    }
}