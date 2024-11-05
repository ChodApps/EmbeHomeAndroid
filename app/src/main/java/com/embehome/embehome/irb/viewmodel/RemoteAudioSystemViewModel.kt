package com.embehome.embehome.irb.viewmodel

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.irb.AudioSystemRemoteInfo
import com.embehome.embehome.irb.RemoteOperations
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.irb.viewmodel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 15-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RemoteAudioSystemViewModel : ViewModel() {

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

    fun submit () {
        performAction("Save")
    }

    var button : AudioSystemRemoteInfo? = null
    fun action (btn : AudioSystemRemoteInfo) {
        if (button == null) {
            button = btn
            performAction ("ClickButton")
        }
    }

    @ExperimentalStdlibApi
    fun action (context: Context, btnName : String, irValue : String, operationType: String, thingsID : Int) = viewModelScope.launch {
       val res =  RemoteOperations.operateRemote(context, btnName, irValue, operationType, thingsID)
        if (res != "Failed" && operationType != "Operate") {
            remoteIRData[btnName] = res
        }
    }

    fun saveRemote (context: Context, macID : String, sno : String, thingsID : Int, remoteName : String, remoteID : Int, category : String, irData : HashMap<String, String>) = viewModelScope.async{
        RemoteOperations.saveRemote(context, macID, sno, thingsID, remoteName, remoteID, category, irData)
    }


}