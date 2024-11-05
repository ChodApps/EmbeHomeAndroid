package com.embehome.embehome.Activity

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.deRegisterNotification
import com.embehome.embehome.registerNotification

class RemoteList : AppCompatActivity() {

    @ExperimentalStdlibApi
    lateinit var callBack : ConnectivityManager.NetworkCallback

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_list)
//        callBack  = Validators.registerToWifi(this)
        findNavController(R.id.fragment7).setGraph(R.navigation.remote_display, intent.extras)
    }

    @ExperimentalStdlibApi
    override fun onDestroy() {
        super.onDestroy()
//        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)
    }


    @ExperimentalStdlibApi
    override fun onStart() {
        super.onStart()
        registerNotification(this, this)
        callBack = Validators.registerToWifi(this)

    }

    @ExperimentalStdlibApi
    override fun onStop() {
        super.onStop()
        deRegisterNotification(this)
        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)

    }

}