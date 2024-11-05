package com.embehome.embehome.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.embehome.embehome.Utils.AppDialogs

open class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        p0?.let {
            try {
                val w = it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                w.getNetworkCapabilities(w.activeNetwork)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED).also {
                    if (it == false) {
                    }else
                        AppDialogs.closeInternetAvailableDialog()
                    }
            }
            catch (e : Exception) {
                Log.e("TronX","MyBroadCast Receiver - ${e.message}")
            }
        }

        Log.d("TronX","reached Broadcast receiver")
    }
}