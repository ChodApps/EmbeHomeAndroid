package com.embehome.embehome.Services

import android.content.Context
import android.util.Log
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.esptouch.EsptouchTask
import com.embehome.embehome.esptouch.IEsptouchResult
import com.embehome.embehome.esptouch.util.TouchNetUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull


/** com.tronx.tt_things_app.Services
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 29-01-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object HubServices {

    private lateinit var mTask: EsptouchTask
    private var isHubConfig = false

    suspend fun getHubDetails(context: Context, password: String): Boolean = withContext(Dispatchers.IO) {
        val ssid = AppServices.wifiSSID(context).toByteArray()
        val bssid = AppServices.wifiBSSID(context)?.let { TouchNetUtil.parseBssid2bytes(it) } ?: "00000000".toByteArray()
        val deviceCount: ByteArray = "1".toByteArray()

        // Call the configuration function with a retry mechanism and timeout
        val result = withTimeoutOrNull(60000) { // 60 seconds timeout
            retryEspTouchConfiguration(context, ssid, bssid, password.toByteArray(), deviceCount)
        }

        result ?: false // Return false if the task timed out
    }

    private suspend fun retryEspTouchConfiguration(
        context: Context,
        ssid: ByteArray,
        bssid: ByteArray,
        pwd: ByteArray,
        deviceCount: ByteArray
    ): Boolean = withContext(Dispatchers.IO) {
        var retryCount = 0
        val maxRetries = 2
        var success = false

        while (retryCount < maxRetries && !success) {
            success = executeEspTouch(context, ssid, bssid, pwd, deviceCount)
            retryCount++
            if (!success) {
                Log.d("TronX", "Retrying EspTouch configuration... Attempt $retryCount")
            }
        }

        if (!success) {
            Log.d("TronX", "EspTouchTask failed after $maxRetries attempts")
        }

        success
    }

    private fun executeEspTouch(
        context: Context,
        ssid: ByteArray,
        bssid: ByteArray,
        pwd: ByteArray,
        deviceCount: ByteArray
    ): Boolean {
        if (isHubConfig) return false

        isHubConfig = true
        try {
            mTask = EsptouchTask(ssid, bssid, pwd, context)
            mTask.setPackageBroadcast(true)
            val mResult = mTask.executeForResults(1)
            val firstResult = mResult[0]

            if (!firstResult.isCancelled && firstResult.isSuc) {
                Log.d("TronX", "EsptouchTask succeeded")
                // Handle success logic, such as storing hub details
                return true
            } else {
                Log.d("TronX", "EsptouchTask failed")
                return false
            }
        } catch (e: Exception) {
            Log.e("TronX", "EsptouchTask encountered an error: ${e.message}")
            return false
        } finally {
            isHubConfig = false
        }
    }

    suspend fun startEspTouch(context: Context, ssid: ByteArray, bssid: ByteArray, pwd: ByteArray, deviceCount: ByteArray) {
        // Optionally implement a method to start the EspTouch task separately
        retryEspTouchConfiguration(context, ssid, bssid, pwd, deviceCount)
    }
}