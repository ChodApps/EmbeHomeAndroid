package com.embehome.embehome.notification

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import kotlinx.coroutines.launch

class NotificationDisplayViewModel : ViewModel() {

    val notificationData  = NotificationData.getNotificationList()
    val performAction = MutableLiveData(false)
    var action = ""

    var limit = 25
    var offset = 0


    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    init {
        viewModelScope.launch {
            notificationData.value?.clear()
            NotificationData.requestNotificationList(limit, offset)
            Log.d("TronX_ViewModel", notificationData.value.toString())
        }
    }

    fun loadMoreNotification (l : Int, o : Int) = viewModelScope.launch {
        NotificationData.requestNotificationList(l, o)
    }

    fun back () {
        performAction("Back")
    }

    fun sendPdf (context : Context, sDate : String, eDate : String) = viewModelScope.launch {
        val res = HttpManager.requestActivityPDF(sDate, eDate)
        if (res.status) {
            AppServices.toastShort(context, "Notification pdf send successful")
            performAction("DismissDialog")
        }
        else {
            AppServices.toastShort(context, "Notification pdf send failed")
        }
        AppDialogs.stopLoadScreen()
    }

    fun openSendPdfDialog () {
        performAction  ("PDF")
    }

    fun selectStartDate () {

    }

    fun selectEndDate () {

    }


}