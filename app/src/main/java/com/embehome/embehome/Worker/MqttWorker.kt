package com.embehome.embehome.Worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.failure
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import com.embehome.embehome.R
import com.embehome.embehome.Repository.DataRequestRepository


/** com.tronx.tt_things_app.Worker
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 18-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class MqttWorker (context: Context, params : WorkerParameters) : CoroutineWorker (context, params) {
    override suspend fun doWork(): Result {
        val appContext = applicationContext
        val res = DataRequestRepository.getAreaData()
        return if (res.size > 0) {
            val noti = NotificationCompat.Builder(appContext, "Temp").apply {
                setSmallIcon(R.drawable.hub_in_lan)
                setContentTitle("Testing Notifications")
                setContentText("You Have successfully implemented the Notification.. Congrats!!")
                priority = NotificationCompat.PRIORITY_MAX
            }
            with(NotificationManagerCompat.from(appContext)) {
                notify(1, noti.build())
            }
            Log.d("TronX","Worker requested for data ${res.toList()}")
            success()
        } else {
            failure()
        }
    }

}