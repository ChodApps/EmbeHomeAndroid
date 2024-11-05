package com.embehome.embehome.instructions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.embehome.embehome.R
import com.embehome.embehome.SwitchImageNames
import com.embehome.embehome.getSwitchImages
import kotlinx.android.synthetic.main._remote_category.view.*
import kotlinx.android.synthetic.main.fragment_switch_icons_list.view.*

class SwitchIconsList (val name : MutableLiveData <String>) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_switch_icons_list, container, false)

        v.recyclerView.adapter = SwitchImageListAdapter (requireContext()) {
            name.value = it
            Log.d("TronX _ Choose icon", it)
            dismiss()
        }
        v.button49.setOnClickListener {
            dismiss()
        }
        return v
    }
}

class SwitchImageListAdapter (val context : Context, val submit : (name : String) -> Unit) : RecyclerView.Adapter<SwitchImageListAdapter.ViewHolder>() {

    val data = SwitchImageNames.values()
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder (LayoutInflater.from(context).inflate(R.layout._remote_category, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.remote_category_select.visibility = View.GONE
        val i = getSwitchImages(data[position].name, false)
        holder.itemView.remoteCategoryIcon.setImageResource(i)

        holder.itemView.setOnClickListener {
            submit(data[position].name)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

}