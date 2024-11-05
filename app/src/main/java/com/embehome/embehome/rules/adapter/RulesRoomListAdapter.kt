package com.embehome.embehome.rules.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.Fragments.RoomSwitchBoardsDirections
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.rules.model.RuleDetails
import com.embehome.embehome.rules.utils.RuleDataRepository
import com.embehome.embehome.rules.utils.RuleOperationRepository
import kotlinx.android.synthetic.main._room_rule.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.rules.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 24-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RulesRoomListAdapter (val context : Context, val data : MutableLiveData<ArrayList<RuleDetails>>?) : RecyclerView.Adapter<RulesRoomListAdapter.ViewHolder> () {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._room_rule, parent, false))
    }

    override fun getItemCount(): Int {
        return data?.value?.size ?: 0
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rule = data?.value?.get(position)
        val ruleItem = RuleDataRepository.getRuleItem(rule?.rulename_id ?: 0)

        if (ruleItem.rulename_id > 0) {
            holder.itemView.room_scene_display_name.text = ruleItem.rule_name
            if (ruleItem.image != null) {
                holder.itemView.imageView26.setImageBitmap(ruleItem.image)
            }
            else {
                GlobalScope.launch (Dispatchers.Main){
                    val res = AppServices.imageSave(context, ruleItem.rule_image)
                    if (res != null) {
                        ruleItem.image = res
                        holder.itemView.imageView26.setImageBitmap(ruleItem.image)
                        notifyItemChanged(position)
                    }
                }
            }
        }
        rule?.let {r ->
            holder.itemView.cardView27.setOnClickListener {
                if (CacheHubData.selectedHubIP.length > 5) {
                    AppDialogs.openTitleDialog(it.context, "Delete Rule","Are you sure you want to delete rule","NO","YES"){ dialog, which ->
                        GlobalScope.launch (Main) {
                            val res = RuleOperationRepository.deleteRule(r.macID, r.rule_id)
                            if (res)  {
                                AppServices.toastShort(context, "Rule deleted successfully")
                                notifyDataSetChanged()
                            }
                            else {
                                AppServices.toastShort(context, "Failed to delete rule from hub")
                            }
                        }
                    }
                }
                else AppServices.toastShort(context, CacheHubData.homeNetwork)

            }

            holder.itemView.cardView22.setOnClickListener {
                if (CacheHubData.selectedHubIP.length > 5) {
                    val d = RoomSwitchBoardsDirections.actionRoomSwitchBoardsToAddRule(r.rule_id)
                    val intent = it.findNavController().navigate(d)
                }
                else AppServices.toastShort(context, CacheHubData.homeNetwork)
            }
        }

    }


}