package com.embehome.embehome.Worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.embehome.embehome.Manager.HttpManager

class FCMRegistration (val context: Context, private val parameters: WorkerParameters) : CoroutineWorker (context, parameters) {
    override suspend fun doWork(): Result {

        return try {
            val token = parameters.inputData.getString("token")
            if (token?.length ?: 0 > 1) {
                val res = HttpManager.addFCMToken(token!!)
               if (res.status) {
                   Log.d("TronX","FCM Registered")
               }
                else {
                   Log.d("TronX",res.body.toString())
               }
            }
            Result.success()
        }
        catch (e : Exception) {
            Result.failure()
        }
    }

}