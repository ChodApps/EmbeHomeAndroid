package com.embehome.embehome.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.notification.model.NotificationCloudInternalModel
import com.embehome.embehome.notification.model.NotificationCloudModel
import com.embehome.embehome.notification.model.NotificationModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

object NotificationData {

    private val notificationData = MutableLiveData<ArrayList<NotificationModel>> (ArrayList())

    fun getNotificationList (): MutableLiveData<ArrayList<NotificationModel>> {

        return notificationData
    }

    suspend fun requestNotificationList (limit : Int, offset : Int) {
        if (notificationData.value?.size?: 0 >= 0) {
            val res = fetchNotificationHttp (limit, offset)
            if (res.status) {
                notificationData.value = parseNotificationData(notificationData.value!!, (res.body as NotificationCloudModel).data)
                notificationData.value = notificationData.value
            }
            else {
                notificationData.value = notificationData.value
            }
        }
        Log.d("TronX", notificationData.value.toString())
    }

    private fun parseNotificationData(list: ArrayList<NotificationModel>, notificationCloudModel: NotificationCloudInternalModel): ArrayList<NotificationModel>? {
        notificationCloudModel.notifications_data.forEach {
            val id = it.notification_id
            val title = it.fcm_notification.title
            val body = it.fcm_notification.body
            val notification = NotificationModel(id, title, body, it.createdAt)
            list.add(notification)
        }
//        list.sortBy {
//            it.notificationId
//        }
        list.sortByDescending { it.notificationId }
        return list
    }

    private suspend fun fetchNotificationHttp (limit : Int, offset : Int) = withContext(Main){
        HttpManager.getNotificationList(limit, offset)
    }

    fun updateNotification (data : NotificationModel) {
        var flag = true
        notificationData.value?.forEach {
           if (data.notificationId == it.notificationId)
               flag = false
        }
        if (flag)  {
            notificationData.value?.add(data)
            notificationData.value?.sortBy {
                it.notificationId
            }
        }

    }

}