package com.embehome.embehome.curtain

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.SwitchDetails
import com.embehome.embehome.Repository.OperationRepository
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.curtain
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 11-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class CurtainDisplayViewModel : ViewModel () {


    val configMode = MutableLiveData (false)
    var action = ""
    val performAction = MutableLiveData (false)
    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    val curtain = MutableLiveData<SwitchDetails>()
    var b : BoardDetails? = null

    fun setConfigMode () {
        performAction("Config")
    }

    fun saveConfig () {
        configMode.value = false
    }

    fun open () {
        performAction("Open")
    }

    fun close () {
        performAction("Close")
    }

    fun stop () {
        performAction("Stop")
    }

    @ExperimentalStdlibApi
    fun playCurtain  (context : Context, deviceID : Int, status :Int, mode : Boolean) = viewModelScope.launch {
        if (mode) {
            LANManager.reConfigCurtain(deviceID, status)
        }
        else {
//            LANManager.operateCurtain(deviceID, status)
            OperationRepository.performSwitchOperation(
                context.applicationContext,
                CacheHubData.selectedMacID,
                CacheHubData.selectedAreaId,
                deviceID,
                1,
                status,
                CacheHubData.selectedHubIP
            )
        }
    }



 }