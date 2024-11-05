package com.embehome.embehome.Activity

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.deRegisterNotification
import com.embehome.embehome.registerNotification

class SceneConfiguration : AppCompatActivity() {


    lateinit var callBack : ConnectivityManager.NetworkCallback

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scene_configuration)
//        callBack  = Validators.registerToWifi(this, lo = {
//            AppServices.toastShort(this, "Internet Connectivity lost.")
//            finish()
//        })
        findNavController(R.id.fragment2).setGraph(R.navigation.scene_add, intent.extras)
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
        callBack  = Validators.registerToWifi(this, lo = {
            AppServices.toastShort(this, "Internet Connectivity lost.")
            finish()
        })
    }

    override fun onStop() {
        super.onStop()
        deRegisterNotification(this)
        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)

    }

}
