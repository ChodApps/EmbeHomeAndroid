package com.embehome.embehome.Activity

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.embehome.embehome.Fragments.*
import com.embehome.embehome.Interface.FragmentHandlerInterface
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.deRegisterNotification
import com.embehome.embehome.registerNotification

class FragmentHandler : AppCompatActivity(), FragmentHandlerInterface {

    lateinit var callBack : ConnectivityManager.NetworkCallback

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_handler)
//        callBack = Validators.registerToWifi(this)

        try {
            val extra = intent.extras
            if (extra != null) {
                val option = extra["fragment"]
                when (option as String) {
                    "setAlert" -> {
                        val macID = extra["macID"]
                        val serialNo = extra["thingsID"]
                        val name = extra["name"]
                        val switchId = extra["switchId"]
                        /* supportFragmentManager.beginTransaction().add(R.id.fragment_container, SetNotificationAlert(
                         macID.toString(),
                         serialNo.toString().toInt(),
                         switchId.toString().toInt(),
                         name.toString()
                     )).commit()*/
                        supportFragmentManager.beginTransaction().add(
                            R.id.fragment_container,
                            SSBSwitchSettings(
                                macID.toString(),
                                serialNo.toString().toInt(),
                                switchId.toString().toInt()
                            )
                        ).commit()
                    }

                    "ConfigCur" -> {
                        val macID = extra["macID"] as String?
                        val serialNo = extra["thingsID"] as String?
                        if (macID == null || serialNo == null)
                            finish()
                        CacheSceneTwoWay.getBoardData(macID!!, serialNo!!)?.let {
                            supportFragmentManager.beginTransaction()
                                .add(R.id.fragment_container, DeviceConfig(it)).commit()
                        }
                    }

                    "createArea" -> {
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container, CreateItem("Area", "Create")).commit()
                    }

                    "editArea" -> {
                        val id = extra["id"] as Int?
                        id?.toInt()?.let {
                            supportFragmentManager.beginTransaction()
                                .add(R.id.fragment_container, CreateItem("Area", "Update", it))
                                .commit()
                        }
                    }

                    "updateArea" -> {
                        val macID = extra["macID"] as String?
                        val serialNo = extra["thingsID"] as Int?
                        if (macID == null || serialNo == null)
                            finish()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container, AreaSettings(macID!!, serialNo!!))
                            .commit()
                    }

                    else -> {
                        finish()
                    }
                }
                /*if (option != null && option == "setAlert") *//*{
                val macID = extra["macID"]
                val serialNo = extra["thingsID"]
                val name = extra["name"]
                val switchId = extra["switchId"]
               *//**//* supportFragmentManager.beginTransaction().add(R.id.fragment_container, SetNotificationAlert(
                    macID.toString(),
                    serialNo.toString().toInt(),
                    switchId.toString().toInt(),
                    name.toString()
                )).commit()*//**//*
                supportFragmentManager.beginTransaction().add(R.id.fragment_container, SSBSwitchSettings(macID.toString(), serialNo.toString().toInt(), switchId.toString().toInt())).commit()
            }*/

            } else {
                finish()
            }
        }
        catch (e : Exception) {
            Log.e("TronX_", "${e.message} Fragment Handler")
            finish()
        }

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
        callBack = Validators.registerToWifi(this)
    }

    @ExperimentalStdlibApi
    override fun onDestroy() {
        super.onDestroy()
//        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)
    }

    override fun gotoTouchSensitivity(board: BoardDetails) {
        if (CacheHubData.selectedHubIP.length > 5)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DeviceTouchSensitivity(board)).addToBackStack("Frag").commit()
        else
            AppServices.toastShort(this, CacheHubData.homeNetwork)
    }

}
