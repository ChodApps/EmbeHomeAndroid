package com.embehome.embehome.irb.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.irb.RemoteButtons
import com.embehome.embehome.irb.RemoteOperations
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.irb.viewmodel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 03-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RemoteMiniTVViewModel : ViewModel() {

    val performAction = MutableLiveData (false)
    var action = ""

    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    var button : RemoteButtons? = null
    fun action (btn : RemoteButtons) {

        if (button == null) {
            button = btn
            performAction ("ClickButton")
        }
    }
//    fun action (btn : RemoteButtons) {
//        performAction (btn.name)
//    }

    @ExperimentalStdlibApi
    fun action (context: Context, btnName : String, irValue : String, operationType: String, thingsID : Int) = viewModelScope.launch {
        val res =  RemoteOperations.operateRemote(context, btnName, irValue, operationType, thingsID)
        button = null
    }

    @ExperimentalStdlibApi
    /*fun play (ip : String, thingsID : Int, ir : String) = viewModelScope.launch {
        IRBOperationRepository.playButton(ip, thingsID, ir)
    }*/

    fun fullRemote () {
        performAction("Full")
    }
}