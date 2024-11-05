package com.embehome.embehome.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.*
import android.util.Log
import android.util.Patterns
import com.embehome.embehome.Services.MqttClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.regex.Pattern


/** com.tronx.tt_things_app.Constants
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 08-01-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object Validators {

      fun email(email : String)  : Boolean =  Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(),email)

      fun password (paswd : CharArray?) : Boolean {
          //if (paswd.con)
          return paswd!!.size >= 8 && containUpperCase(paswd) && containNumber(paswd)
      }

    fun passwordWifi  (paswd : CharArray?) : Boolean {
        return paswd!!.size >= 0
    }

    private fun containUpperCase (paswd : CharArray?) : Boolean {
        paswd?.forEach {
            if (it.isUpperCase())
                return true
        }
        return false
    }

    private fun containNumber(paswd : CharArray?) : Boolean {
        paswd?.forEach {
            if (it.isDigit())
                return true
        }
        return false
    }

    fun cnfPassword (paswd : String, cnfpaswd : String) : Boolean = paswd == cnfpaswd

     fun mobile (mob : String) : Boolean = mob.length >= 10

    lateinit var currentNetwork: Network

    @ExperimentalStdlibApi
    fun registerToWifi (context : Activity, av : () -> Unit = {}, lo : () -> Unit = {}): ConnectivityManager.NetworkCallback {
        val builder = NetworkRequest.Builder()
        /*builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_VPN)
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)*/


        val callBack = object : ConnectivityManager.NetworkCallback () {

           /* override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                if (!::currentNetwork.isInitialized) {
                    currentNetwork = network
                }
                Log.d("TronX","network onBlockNetworkStatusChanged $blocked $network")
                AppDialogs.closeInternetAvailableDialog()
            }*/

    /*        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                if (!::currentNetwork.isInitialized) {
                    currentNetwork = network
                }
                Log.d("TronX","network onLinkPropertiesChanged  $network")
                AppDialogs.closeInternetAvailableDialog()
                try {
                    MqttClient.onLinkChange(context)
                    CacheHubData.getAllIpConnectedInNetwork()
                }
                catch (e : Exception) {
                    Log.e("TronX","onLinkChanged - ${e.message}  ")
                }
            }*/

    /*        override fun onLosing(network: Network, maxMsToLive: Int) {
                if (!::currentNetwork.isInitialized) {
                    currentNetwork = network
                }
                Log.d("TronX","network onLosing  $network")
                AppDialogs.closeInternetAvailableDialog()
            }*/

         /*   override fun onUnavailable() {
                Log.d("TronX","network onUnavailable  ")
            }*/

            override fun onAvailable(network: Network) {
                AppDialogs.closeInternetAvailableDialog()
                if (!::currentNetwork.isInitialized) {
                    currentNetwork = network
                }
                Log.d("TronX","network found")
                if (network != currentNetwork) {
//                    currentNetwork = network
                    GlobalScope.launch(Dispatchers.Main) {
//                    CacheHubData.getAllIpConnectedInNetwork()
//                        AppDialogs.closeInternetAvailableDialog()
                        av()
                        /*AppDialogs.stopLoadScreen()
                    AppServices.toastShort(context, "Internet Connected")*/
                    }
                }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (!::currentNetwork.isInitialized) {
                    currentNetwork = network
                }
                Log.d("TronX","network changed  $network")

                if (currentNetwork != network) {
                    currentNetwork = network
                    GlobalScope.launch(Main) {
                        /*AppDialogs.closeInternetAvailableDialog()
                    CacheHubData.getAllIpConnectedInNetwork()*/
                        AppDialogs.closeInternetAvailableDialog()
                        try {
                            MqttClient.onLinkChange(context)
                            CacheHubData.getAllIpConnectedInNetwork()
                        } catch (e: Exception) {
                            Log.e("TronX", "onChangeException - ${e.message}  ")
                        }
                    }
                }
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onLost(network: Network) {
                Log.d("TronX","network lost $network")
                if (!::currentNetwork.isInitialized) {
                    currentNetwork = network
                }
                if (currentNetwork == network)
                GlobalScope.launch (Dispatchers.Main) {
                    AppDialogs.openInternetAvailableDialog(context)
                    CacheHubData.getAllIpConnectedInNetwork()
                    lo()
                }
            }

            /*override fun onUnavailable() {
                super.onUnavailable()
                Log.d("TronX","network unavailable")
                GlobalScope.launch (Dispatchers.Main) {
                    AppDialogs.openMessageDialog(context, "Internet not available")
                    CacheHubData.getAllIpConnectedInNetwork()
                    lo()
                }
            }*/
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(builder.build(), callBack)
        return callBack
    }

    fun unregisterToWifi (context: Activity, callBack : ConnectivityManager.NetworkCallback) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(callBack)
    }
}