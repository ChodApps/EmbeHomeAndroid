package com.embehome.embehome.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.R
import com.embehome.embehome.notification.model.NotificationModel
import kotlinx.android.synthetic.main._notification_display_item.view.*

class NotificationListAdapter(val context: Context, val data: MutableLiveData<ArrayList<NotificationModel>>) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder> () {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout._notification_display_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: NotificationListAdapter.ViewHolder, position: Int) {
        data.value?.get(position)?.let {
            holder.itemView.notification_item_title.text = it.title
            holder.itemView.notification_item_body.text = it.body
            val date = it.time.replace("T", "  ")
            holder.itemView.textView212.text = date.replace(".000Z", "")
        }
    }
}

