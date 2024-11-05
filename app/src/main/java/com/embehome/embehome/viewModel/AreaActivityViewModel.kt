package com.embehome.embehome.viewModel

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Manager.SSBManager
import com.embehome.embehome.Model.SwitchDetails
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.irb.CacheRemoteData
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 16-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class AreaActivityViewModel : ViewModel(){

    var boards = CacheHubData.getBoardDetails(CacheHubData.selectedMacID, CacheHubData.selectedAreaId)
    val areaID = MutableLiveData(CacheHubData.selectedAreaId)
    val selectedBoard = MutableLiveData<Int>()

    val remotes = MutableLiveData<ArrayList<RemoteCloudModel>>()
    val selectedRemote = MutableLiveData<RemoteCloudModel> ()
    val deviceType = MutableLiveData<String>()

    val selectSwitchDetail = MutableLiveData<Int>(0)
    val selectedSwitch = MutableLiveData<Int>()
    val switchData = MutableLiveData<ArrayList<SwitchDetails>>()
    val switchUpdate = MutableLiveData<Boolean>()
    val sleep = MutableLiveData<Boolean>(false)
    val switchDetailName = MutableLiveData<String>("Switch Detail")
    val switchDetailImage = MutableLiveData<Int>()

    val showSleepBtn = MutableLiveData(View.VISIBLE)
    val isSetting = MutableLiveData (false)
    val fragmentTitle = MutableLiveData<String>()
    val toggleToPerformAction = MutableLiveData<Boolean>(false)
    val switchAlert = MutableLiveData(false)
    var operation = ""

    val switchListView = MutableLiveData(View.VISIBLE)
    val switchDetailView = MutableLiveData(View.VISIBLE)

    val selectedCurtainStatus = MutableLiveData<Int>()

    fun getRemoteList (macID :String, sno : String) = viewModelScope.launch {
        val data = CacheRemoteData.getIRBRemoteList(macID, sno)
        if (data.size > 0) {
            remotes.value = data
            selectedRemote.value = data[0]
        }
        else remotes.value?.clear()
        performUIClickAction("refreshSwitchList")
    }

    fun openSetAlert () {
        performUIClickAction("setAlert")
    }

    fun openPowerChart () {
        performUIClickAction("Power")
    }

    fun updateArea () {
        performUIClickAction("Update")
    }

    @ExperimentalStdlibApi
    fun sleep() {
        performUIClickAction("SLEEP")
    }

    fun areaFragBack() {
        performUIClickAction("BACK")
    }


    fun delete () {
        performUIClickAction("DeleteBoard")
    }

    @ExperimentalStdlibApi
    fun deleteBoard(context : Context) =
        viewModelScope.launch {
//            displayToast("Deleting Board ")
            if (CacheHubData.selectedHubIP.length > 1) {
                boards.value?.let {bl ->
                    selectedBoard.value?.let {sb ->
                        if (bl.size > sb) {
                            AppDialogs.startLoadScreen(context)
                            val delResult = SSBManager.deleteSSB(bl[sb].thing_serial_number, bl[sb].thing_id)
                            if (delResult.status) {
                                displayToast("Board Deleted Successfully")
                                // removeBoard(serialNo)
                                areaFragBack()
                            }
                            else {
                                displayToast(delResult.data)
                            }
                            AppDialogs.stopLoadScreen()
                        }
                        else displayToast("Unable to delete board.")
                    }
                }

//                val serialNo = boards.value!![selectedBoard.value!!].thing_serial_number

            }
            else {
                displayToast(CacheHubData.homeNetwork)
            }
        }


    @ExperimentalStdlibApi
    fun deleteDevice (context : Context) = viewModelScope.launch {

        boards.value?.let {bl ->
            selectedBoard.value?.let {sb ->
                if (bl.size > sb) {
                    val type = bl[sb].thing_type
                    AppDialogs.openTitleDialog(context, "Delete $type", "Are you sure you want to delete $type", "NO","Yes") {d, w ->
                        deleteBoard(context)
                    }
                }
                else displayToast("Unable to delete board.")
            }
        }
    }

    @ExperimentalStdlibApi
    fun performAction(deviceID: Int, switchID: Int, deviceStatus: Int) {
        viewModelScope.launch {
            val actionResult = LANManager.performAction(
                deviceID,
                switchID,
                deviceStatus
            )
            if (actionResult != null && actionResult.size == boards.value!![selectedBoard.value!!].switchesList.size) {
                updateSwitch(actionResult)
            }
            else{
                displayToast("Operation Failed")
            }
        }
    }

    private fun updateSwitch(switchData : List<String>) {
        sleep.value = false
        switchData.forEachIndexed{i , s ->
            boards.value!![selectedBoard.value!!].switchesList[i].switchstatus = s.toInt()
            if (s.toInt() > 0 ) sleep.value = false
        }

        switchUpdate.value = true
        switchUpdate.value = false
    }

    fun setSleepStatus()  {
        sleep.value = true
        try {
            when (boards.value!![selectedBoard.value!!].thing_type) {
                "SSB", "ISB", "RSB" -> {
                    showSleepBtn.value = View.VISIBLE
                    switchDetailView.value = View.VISIBLE
                    switchListView.value = View.VISIBLE
                    boards.value!![selectedBoard.value!!].switchesList.forEach {
                        if (it.switchstatus > 0 ) sleep.value = false
                    }
                }

                "IRB" -> {
                    switchDetailView.value = View.VISIBLE
                    switchListView.value = View.VISIBLE
                    showSleepBtn.value = View.GONE
                }
                 "CUR" -> {
                     switchDetailView.value = View.GONE
                     switchListView.value = View.GONE
                     showSleepBtn.value = View.GONE
                 }
            }
        }
        catch (e : Exception) {
            AppServices.log(e.message.toString())
        }

    }



    private fun displayToast(msg : String) {
        performUIClickAction(msg)
    }

    private fun performUIClickAction(action : String) {
        operation = action
        toggleToPerformAction.value = true
        toggleToPerformAction.value = false
    }

}