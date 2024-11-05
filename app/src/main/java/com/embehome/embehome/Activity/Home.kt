package com.embehome.embehome.Activity

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.deRegisterNotification
import com.embehome.embehome.registerNotification


class Home : AppCompatActivity() {

    @ExperimentalStdlibApi
    lateinit var callBack : ConnectivityManager.NetworkCallback

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        callBack  = Validators.registerToWifi(this, av = {
//            Log.d("TronX","checking hubs")
//            CacheHubData.getMacIdList()
//        })

        /*val builder = NetworkRequest.Builder()
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_VPN)

        callBack = object : ConnectivityManager.NetworkCallback () {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("TronX","network found")
                GlobalScope.launch (Dispatchers.Main) {
                    AppDialogs.stopLoadScreen()
                    CacheHubData.getMacIdList()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("TronX","network lost")
                AppDialogs.startMsgLoadScreen(this@Home, "Internet Connection lost")
                GlobalScope.launch (Dispatchers.Main) {
                    CacheHubData.getMacIdList()
                }
            }
        }
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(builder.build(), callBack)*/

        /*val builder = NetworkRequest.Builder()
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_VPN)

        val callBack = object : ConnectivityManager.NetworkCallback () {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("TronX","network found")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("TronX","network lost")
            }
        }
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(builder.build(), callBack)*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("TronX","Activity receive the permission")
    }

    @ExperimentalStdlibApi
    override fun onStart() {
        super.onStart()
        registerNotification(this, this)
        callBack  = Validators.registerToWifi(this, av = {
            Log.d("TronX","checking hubs")
            CacheHubData.getMacIdList()
        })
    }

    @ExperimentalStdlibApi
    override fun onStop() {
        super.onStop()
        deRegisterNotification(this)
        if (::callBack.isInitialized)Validators.unregisterToWifi(this, callBack)
    }

    @ExperimentalStdlibApi
    override fun onDestroy() {
        super.onDestroy()
//        if (::callBack.isInitialized)Validators.unregisterToWifi(this, callBack)
    }

}
