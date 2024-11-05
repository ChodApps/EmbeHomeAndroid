package com.embehome.embehome

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.Utils.sendNotification
import com.embehome.embehome.Worker.FCMRegistration
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyFireBaseMessageService : FirebaseMessagingService (){

    private val TAG = "TronX _ firebase"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("TronX _ firebase", "from ${message.from}")

        message.notification.let {
            if (it != null) {
                Log.d(TAG, "Message Notification Body: ${it.body}")

                /*it.sound?.let {s ->
                    when (s) {
                        "bell" -> {
                            R.raw.bell
                        }
                        else -> {
                            null
                        }
                    }?.let {
                        MediaPlayer.create(applicationContext, it).start()
                    }
                }*/
                //                sendNotification(it.body!!)
                GlobalScope.launch (Main){
                    notificationReceived.value = "${it.title} \n ${it.body}"
                }

            }
        }
        Log.d("TronX _ firebase", "Message Received ${message.data}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token : String) {
        AppServices.saveToken(applicationContext, "fcm", token)
        val res = AppServices.tempGetToken(applicationContext)
        if (res.length > 5) {
            FakeDB.ApplicationToken = res
            val data = workDataOf(token to token)
            val req = OneTimeWorkRequestBuilder<FCMRegistration>().setInputData(data).build()
            WorkManager.getInstance(applicationContext).enqueue(req)
        }

    }

    private fun sendNotification(messageBody: String) {
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(messageBody, applicationContext)
    }
}
