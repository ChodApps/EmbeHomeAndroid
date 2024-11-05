package com.embehome.embehome.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.R
import kotlinx.android.synthetic.main._image_grid.view.*


/** com.tronx.tt_things_app.Adapter
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 28-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class ImageGridAdapter (val context : Context) : RecyclerView.Adapter<ImageGridAdapter.ViewHolder>() {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout._image_grid, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 50
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.image_grid_image.setOnClickListener{
            Toast.makeText(context, "Selected Image $position", Toast.LENGTH_SHORT).show()
        }
    }
}