package com.embehome.embehome.scene.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.SceneSwitchDetailModel
import com.embehome.embehome.Repository.HubFeatureHandleRepository
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.irb.CacheRemoteData
import com.embehome.embehome.irb.model.RemoteCloudModel
import com.embehome.embehome.scene.IRSceneModel
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.scene.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 12-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class EditSceneViewModel : ViewModel() {

    val save = "Save Scene"
    val update = "Update Scene"

    var sceneNameId = 0

    private val irHubSize = 950

    val subScene = MutableLiveData<ArrayList<Int>>()

    val testMessage = MutableLiveData("")
    val remoteName = MutableLiveData<String>()
    val selectedRemote = MutableLiveData<RemoteCloudModel>()
    val remoteList = MutableLiveData<ArrayList<RemoteCloudModel>>()
    val irbName = MutableLiveData("IRB Not Found")
    val irbSelected = MutableLiveData<BoardDetails>()
    val deviceSelected = MutableLiveData<ArrayList<SceneSwitchDetailModel>>()
    val oldSceneID = MutableLiveData<Int>()
    var irbList = ArrayList(CacheHubData.getAllIRBList(CacheHubData.selectedMacID)).also {
        Log.d("TronX",it.toString())
        if (it != null && it.isNotEmpty()) {
            irbSelected.value = it[0]
            irbName.value = irbSelected.value!!.thing_name
        }
    }

    val irbSaveButton = MutableLiveData<String>(save)
    var sceneEdit = false

    fun back () {
        performAction("Back")
    }

    val selectedButtonList = MutableLiveData<ArrayList<IRSceneModel>>(ArrayList())
    val selectedButton = MutableLiveData<String>()


    val editView = MutableLiveData(View.VISIBLE)
    val timeInterval = MutableLiveData<String>("0")
    fun selectRemoteBtn () {
        val key = "KEY_${selectedButton.value?.replace(' ','_')}"
        val ir = selectedRemote.value?.ir_data?.get(key) ?: "Corrupt Data"
        addSelectedRemoteButton(irbSelected.value!!.thing_id, selectedRemote.value!!.remote_id, selectedRemote.value!!.remote_name, key, ir, timeInterval.value?.toFloat() ?: 0.0f)
    }

    private fun removeIRB (sno: String) {
        irbList.removeAll(
        irbList.filter {
            it.thing_serial_number == sno
        })
        if (irbList.size > 0) {
            irbSelected.value = irbList[0]
        }
    }

    fun getRemote (macID : String, sno : String) = viewModelScope.launch {
        val r = ArrayList(CacheRemoteData.getRemoteForScene(macID, sno))
        if (r.size <= 0 )
            removeIRB(sno)
        remoteList.value = r
    }


    val actionTrigger = MutableLiveData(false)
    var action = ""

    fun performAction (operation : String) {
        action = operation
        actionTrigger.value = true
        actionTrigger.value = false
    }

    fun selectIRB () {
        performAction("selectIRB")
    }

    fun changeRemote () {
        performAction("selectRemote")
    }

    var toastText = ""

    private fun addSelectedRemoteButton (tid : Int, rid : Int, rName : String, kName : String, kValue : String, time : Float) {
        var tSize = kValue.toByteArray().size
        selectedButtonList.value!!.forEach {
            tSize += it.data.toByteArray().size
        }
        if (tSize <= irHubSize && selectedButtonList.value!!.size < 5) {

            val btn = IRSceneModel(tid, rid, kValue, rName, kName, time)
            selectedButtonList.value?.add(btn).also {
                performAction("Refresh")
            }
        }
        else {
            toastText = "You Reached the limit to add any more Buttons to this scene"
            performAction("Toast")
        }
        checkEditOption()
    }

    fun checkEditOption () {
         editView.value  = if (selectedButtonList.value != null && selectedButtonList.value!!.size > 0 ) View.GONE else View.VISIBLE
    }

    @ExperimentalStdlibApi
    fun saveScene () = viewModelScope.launch {
        if (sceneEdit) {
            if (selectedButtonList.value!!.size > 0) {
                performAction("loadOn")
                val res = HubFeatureHandleRepository.updateSceneRemote(
                    CacheHubData.selectedMacID,
                    CacheHubData.selectedHubIP,
                    sceneNameId, oldSceneID.value ?: 0,true, deviceSelected.value ?: ArrayList(),
                    selectedButtonList.value ?: ArrayList(), subScene.value ?: ArrayList()
                )
                if (res) {
                    performAction("Exit")
                } else
                    performAction("loadOff")
            } else {
                toastText = "Please add at least one Button"
                performAction("Toast")
            }
        } else {
            if (selectedButtonList.value!!.size > 0) {
                performAction("loadOn")
                val res = HubFeatureHandleRepository.addScene(
                    CacheHubData.selectedMacID,
                    CacheHubData.selectedHubIP,
                    sceneNameId,
                    irData = selectedButtonList.value!!,
                    subScene = subScene.value ?: ArrayList()
                )
                if (res) {
                    performAction("Exit")
                } else
                    performAction("loadOff")
            } else {
                toastText = "Please add at least one Button"
                performAction("Toast")
            }
        }
    }
}