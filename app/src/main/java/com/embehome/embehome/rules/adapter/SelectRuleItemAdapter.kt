package com.embehome.embehome.rules.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.rules.fragment.RuleTypeSelectionDirections
import com.embehome.embehome.rules.model.RuleItemDetails
import kotlinx.android.synthetic.main._image_list_big.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.rules.adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 25-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SelectRuleItemAdapter  (val context: Context, val data : ArrayList<RuleItemDetails>, val action : (id : RuleItemDetails) -> Unit) : RecyclerView.Adapter<SelectRuleItemAdapter.ViewHolder> () {
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
        holder.itemView.textView167.text = scene.rule_name
        if (scene.image != null) {
            holder.itemView.imageView227.setImageBitmap(scene.image)
        }

        else {
            holder.itemView.imageView227.setImageResource(R.drawable.place_holder_2)
            GlobalScope.launch (Dispatchers.Main) {
                val res = AppServices.imageSave(context, scene.rule_image)
                if (res != null && scene.image == null) {
                    scene.image = res
                    /*holder.itemView.imageView227.setImageBitmap(scene.image)
                    notifyItemChanged(position)*/
                    notifyDataSetChanged()
                }
            }
        }

        if (!scene.default_rule) {
            holder.itemView.cardView22.visibility = View.VISIBLE
        }
        else {
            holder.itemView.cardView22.visibility = View.GONE
        }

        holder.itemView.cardView22.setOnClickListener {
            val d = RuleTypeSelectionDirections.actionRuleTypeSelectionToCreateArea4("Rule", "Update", scene.rulename_id)
            it.findNavController().navigate(d)
        }

        holder.itemView.setOnClickListener {
            action(data[position])
        }
    }
}