/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.embehome.embehome.Utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.embehome.embehome.Activity.MainActivity
import com.embehome.embehome.CHANNEL_ID
import com.embehome.embehome.R

// Notification ID.
private var NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

/**
 * Builds and delivers the notification.
 *
 * @param messageBody, notification text.
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {


    val contentIntent = Intent(applicationContext, MainActivity::class.java)
//    contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    NOTIFICATION_ID = System.currentTimeMillis().toInt()
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
    )
    /*val contentPendingIntent:PendingIntent
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )
    } else {
        PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }*/

   /* val mp = MediaPlayer.create (applicationContext, R.raw.bell)
    mp.start()*/


    val builder = NotificationCompat.Builder(
        applicationContext,
        CHANNEL_ID
    )   .setSmallIcon(R.drawable.app_icon)
        .setContentTitle(applicationContext.resources.getString(R.string.channel_name))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setAutoCancel(false)
    notify(NOTIFICATION_ID, builder.build())
    NOTIFICATION_ID += 1
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}