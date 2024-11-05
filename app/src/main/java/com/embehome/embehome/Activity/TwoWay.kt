package com.embehome.embehome.Activity

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.embehome.embehome.R
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.deRegisterNotification
import com.embehome.embehome.registerNotification

class TwoWay : AppCompatActivity() {


    lateinit var callBack : ConnectivityManager.NetworkCallback

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_way)
//        callBack  = Validators.registerToWifi(this)
    }

    @ExperimentalStdlibApi
    override fun onDestroy() {
        super.onDestroy()
//        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)
    }

    override fun onStop() {
        super.onStop()
        deRegisterNotification(this)
        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)

    }

    @ExperimentalStdlibApi
    override fun onStart() {
        super.onStart()
        registerNotification(this, this)
        callBack  = Validators.registerToWifi(this)

    }

}
