package com.embehome.embehome.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HUBManager
import com.embehome.embehome.Model.HubUdpData
import com.embehome.embehome.Repository.HubHandleRepository
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.FakeDB
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 19-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class AddHubViewModel : ViewModel() {
    val hubMessage = MutableLiveData<String>()
    val hubNameError = MutableLiveData<Boolean>()
    val hubTitle = MutableLiveData<String>("ADD HUB")
    val imageRef = MutableLiveData<String>()
    val saveHub = MutableLiveData<Boolean>()
    val skipHub = MutableLiveData<Boolean>()
    val hubName = MutableLiveData<String>()
    val addHubBack = MutableLiveData<Boolean>()
    val toastString = MutableLiveData<String>()
    val showToast = MutableLiveData<Boolean>()
    val swapView = MutableLiveData<Boolean>(true)
    val actionBtn = MutableLiveData<String>("SAVE")


    var action = ""
    val performAction = MutableLiveData (false)
    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    init {
        hubMessage.value = "Hub Found Successfully!"
        hubNameError.value = false
        saveHub.value = false
        skipHub.value = false
        showToast.value = false
        hubName.value = "EmbeHome 1.0"
    }

    fun addHubBack() {
        addHubBack.value = true
        addHubBack.value = false
    }

    fun saveHub() {
//        saveHub.value = true
//        saveHub.value = false
        performAction("AddDevice")
    }

    fun skipHub() {
        skipHub.value = true
    }

    fun changeView () {
        swapView.value = swapView.value == false
    }

    @ExperimentalStdlibApi
    fun addHub(wifiSSID : String) {
        viewModelScope.launch {
            val result = HUBManager.addHub(FakeDB.packet, hubName.value.toString(), wifiSSID)
            toastString.value = result.data.also {
                showToast.value = true
                showToast.value = false
            }
            if (result.status) addHubBack()
        }
    }

    @ExperimentalStdlibApi
    fun tempAddHub (context: Context, wifiSSID: String, hubName : String, image : String, hub : HubUdpData) = viewModelScope.launch {
        val result = HubHandleRepository.addHub(context, hub, hubName, image, wifiSSID)
        if (result) delayHubScreen()//addHubBack()
    }

    private fun delayHubScreen () = viewModelScope.launch{
        delay(6000)
        AppDialogs.stopLoadScreen()
        addHubBack()
    }

    fun updateHubData (context: Context, macID : String, name : String, image : String, hubVersion : String, wifiSSID: String) = viewModelScope.launch {
        val result = HubHandleRepository.updateHubData(context, macID, name, image, hubVersion,wifiSSID)
        if (result) addHubBack()
    }

}