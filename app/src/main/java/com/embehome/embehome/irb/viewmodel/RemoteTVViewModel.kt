package com.embehome.embehome.irb.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.irb.CacheRemoteData
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.irb.viewmodel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 01-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RemoteTVViewModel : ViewModel () {


    val performAction = MutableLiveData (false)
    var thingsID = 0
    var macID = ""
    var sno = ""
    var remoteID = 0
    val remoteName = MutableLiveData ("TV")
    var action = ""

    val editMode = MutableLiveData(View.GONE)
    var operationType  = ""

    var remoteIRData = getIrData()

    fun getIrData () : HashMap<String,String> {


        return HashMap()
    }

    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    fun back () {
        performAction("Back")
    }

    fun setEditMode (operationType : String) {
        when (operationType) {
            "Create" -> {
                editMode.value = View.VISIBLE
            }

            "Operate" -> {
                editMode.value = View.GONE
            }
        }
    }

    fun saveRemote () = viewModelScope.launch {
        val remote = RemoteCloudModel (macID, sno, thingsID, remoteName.value!!, remoteID, "TV","",remoteIRData)
        val res = HttpManager.addRemote(remote)
        if (res.status) {
            CacheRemoteData.addCreateRemote(sno, remote)
            performAction("Remote Created Successfully")
            performAction("Exit")
        }
        else
            performAction("failed ${res.body}")
    }


    @ExperimentalStdlibApi
    /*fun action (btn : TVRemoteInfo) = viewModelScope.launch{
        //performAction(btn.name.substring(4))
        if (remoteIRData[btn.name].isNullOrEmpty() && operationType != "Operate") {
            val ir = IRBOperationRepository.readIRValue(CacheHubData.selectedHubIP, thingsID)
            if (ir != "Failed") {
                remoteIRData[btn.name] = ir
                performAction ("IR Value Captures Successfully")
            }

            //readIRValue(btn.name)
        }
        else if (!remoteIRData[btn.name].isNullOrEmpty()) {
            performAction("playing ${btn.name.substring(4)}")
            if (CacheHubData.selectedHubIP.length > 5) {
                LANManager.sendIRData(
                    CacheHubData.selectedHubIP,
                    thingsID,
                    remoteIRData[btn.name].toString()
                )
            }
            else {
                MqttClient.publishOnMqtt(CacheHubData.selectedMacID, LANManager.irbPlayCmd(thingsID, 38000, remoteIRData[btn.name].toString()))
            }
        }
        else
            performAction("You did not perform IR read operation for this button at the time of creation")
    }*/


    fun readIRValue (btnName : String) = viewModelScope.launch {
        val res = LANManager.readIRValue(CacheHubData.selectedHubIP, thingsID)
        if (res.status) {
            val irvalue = res.data.removeRange(0,13)
            remoteIRData[btnName] = irvalue
            performAction(irvalue)
        }
    }


}