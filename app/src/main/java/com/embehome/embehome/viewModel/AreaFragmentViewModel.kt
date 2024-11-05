package com.embehome.embehome.viewModel

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Manager.SSBManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.SwitchDetails
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.irb.CacheRemoteData
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 26-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class AreaFragmentViewModel : ViewModel() {

    var boards = MutableLiveData<ArrayList<BoardDetails>>(ArrayList())
    val areaID = MutableLiveData<Int>()
    val selectedBoard = MutableLiveData<Int>()

    val remotes = MutableLiveData<ArrayList<RemoteCloudModel>>()
    val selectedRemote = MutableLiveData<RemoteCloudModel> ()

    val selectSwitchDetail = MutableLiveData<Int>(0)
    val selectedSwitch = MutableLiveData<Int>()
    val switchData = MutableLiveData<ArrayList<SwitchDetails>>()
    val switchUpdate = MutableLiveData<Boolean>()
    val sleep = MutableLiveData<Boolean>(true)
    val switchDetailName = MutableLiveData<String>("Switch Detail")
    val switchDetailImage = MutableLiveData<Int>()

    val showSleepBtn = MutableLiveData(View.VISIBLE)
    val fragmentTitle = MutableLiveData<String>()
    val toggleToPerformAction = MutableLiveData<Boolean>(false)
    val switchAlert = MutableLiveData(false)
    var operation = ""

    val switchListView = MutableLiveData(View.VISIBLE)
    val switchDetailView = MutableLiveData(View.VISIBLE)


    fun getRemoteList (macID :String, sno : String) = viewModelScope.launch {
        val data = CacheRemoteData.getIRBRemoteList(macID, sno)
        if (data.size > 0) {
            remotes.value = data
            selectedRemote.value = data[0]
            performUIClickAction("refreshSwitchList")
        }
    }

    fun openSetAlert () {
        performUIClickAction("setAlert")
    }

    fun updateArea () {
        performUIClickAction("Implement Area Edit Screen")
    }

    @ExperimentalStdlibApi
    fun sleep() {
        performUIClickAction("SLEEP")
    }

    fun areaFragBack() {
        performUIClickAction("BACK")
    }

    @ExperimentalStdlibApi
    fun deleteBoard() {
        viewModelScope.launch {
            displayToast("Deleting Board ")
            if (CacheHubData.selectedHubIP.length > 1) {
                val serialNo = boards.value!![selectedBoard.value!!].thing_serial_number
                val delResult = SSBManager.deleteSSB(boards.value!![selectedBoard.value!!].thing_serial_number, boards.value!![selectedBoard.value!!].thing_id)
                if (delResult.status) {
                    displayToast("Board Deleted Successfully")
                    // removeBoard(serialNo)
                    areaFragBack()
                }
                else {
                    displayToast(delResult.data)
                }
            }
            else {
                displayToast(CacheHubData.homeNetwork)
            }
        }
    }

    @ExperimentalStdlibApi
    fun performAction(deviceID : Int, switchID: Int, deviceStatus: Int) {
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
        sleep.value = true
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
                "SSB" -> {
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

    fun getBoardData (context : Context, macID: String, areaId : Int) = viewModelScope.launch {
        /*val data = DeviceHandleRepository.getDeviceList(context, macID, areaId)
        data.forEach {
            boards.value?.add(getDeviceFromDao(it))
        }*/
        boards.value = boards.value
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