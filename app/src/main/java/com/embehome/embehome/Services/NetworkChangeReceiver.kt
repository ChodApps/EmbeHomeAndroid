package com.embehome.embehome.Services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.embehome.embehome.Utils.AppDialogs

open class NetworkChangeReceiver : BroadcastReceiver() {


    @OptIn(ExperimentalStdlibApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val connectivityManager = it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val isConnected = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true

            if (isConnected) {
                AppDialogs.closeInternetAvailableDialog()
//                MqttClient.connect(context, "your_mac_id")
            } else {
                // Handle disconnection logic if necessary
            }
        }
    }
}