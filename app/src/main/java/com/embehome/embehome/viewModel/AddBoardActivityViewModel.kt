package com.embehome.embehome.viewModel

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Model.AreaData
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Repository.HubHandleRepository
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.getDeviceType
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 16-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class AddBoardActivityViewModel : ViewModel() {

    val sImage = "AREA"
    val sAdd = "ADD"
    val sConfig = "CONFIGURATION"

    val macID = MutableLiveData<String>()
    val addBoardBack = MutableLiveData<Boolean>()
    val addSSB = MutableLiveData<Boolean>()
    val createArea = MutableLiveData<Boolean>()
    val boardName = MutableLiveData<String>("")
    val areaName = MutableLiveData<String>()
    val selectBoard = MutableLiveData<Boolean>()
    val showToast = MutableLiveData<Boolean>()
    val toastMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val area = MutableLiveData<AreaData>()

    val selectAreaView = MutableLiveData<Int>(View.VISIBLE)
    val addDeviceView = MutableLiveData<Int>(View.GONE)
    val configDevice = MutableLiveData(View.GONE)


    var action = ""
    val performAction = MutableLiveData (false)
    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    init {
        areaName.value = "Area Name"
    }

    fun updateView (op : String) {
        if (op == sImage) {
            selectAreaView.value = View.VISIBLE
            addDeviceView.value = View.GONE
            configDevice.value = View.GONE
        }
        else if (op == sConfig) {
            selectAreaView.value = View.GONE
            addDeviceView.value = View.GONE
            configDevice.value = View.VISIBLE
        }
        else {
            selectAreaView.value = View.GONE
            addDeviceView.value = View.VISIBLE
            configDevice.value = View.GONE
        }
    }


//    IRB -> BAAB^STATUS^SERIAL_NUMBER^THINGS_ID^IRB
//    SSB -> BAAB^STATUS^SERIAL_NUMBER^THINGS_ID^SSB^SWITCH_LIST
    @ExperimentalStdlibApi
    fun addSSB() = viewModelScope.launch {
            loading.value = true
//            val ssbID = CacheHubData.generateHubID(CacheHubData.selectedMacID)
//            val result = LANManager.addDevice(ssbID.toString())
//            if (result.status) {
//                display("Board added to HUB successfully")
//                Log.d("TronX", "Added Board data ${result.data}")
//                val res = SSBManager.prepareBoardAddData(
//                    result.data,
//                    area.value?.area_id ?: 0,
//                    boardName.value ?: "new Board",
//                    ssbID
//                )
//                if (res.thing_serial_number != "") {
//                    Log.d("TronX","Data to send ${res.thing_serial_number} ${res.thing_id} ${CacheHubData.selectedMacID}")
//                    val httpResult = HttpManager.addSwitchBoard( CacheHubData.selectedMacID, res)
//                    if (httpResult.status) {
//                        display("Device added to Cloud successfully")
//                        CacheHubData.setAreaWithData(CacheHubData.selectedMacID, res.area_id, res)
//                        addBoardBack()
//                    } else {
//                        display(
//                            httpResult.body as String
//                        )
//                        display("Reset board and try again")
//                    }
//                }
//            } else
//                display(result.data)
    performAction("AddDevice")
            loading.value = false
        }

    var board : BoardDetails? = null

    @ExperimentalStdlibApi
    fun tempAddDevice (context: Context, areaID : Int, deviceName : String) = viewModelScope.launch {
        AppDialogs.startLoadScreen(context)
        val res = HubHandleRepository.addDevice(context, areaID, deviceName, CacheHubData.selectedMacID)
        AppDialogs.stopLoadScreen()
        if (res.status)  {
            val b = res.body as BoardDetails
            if (b.thing_type == "SSB" || b.thing_type == "CUR" || b.thing_type == "ISB" || b.thing_type == "RSB") {
                AppDialogs.openTitleDialog(
                    context,
                    getDeviceType(b.thing_type),
                    "${getDeviceType(b.thing_type)} added successfully",
                    "Config",
                    { d, i ->
                        board = b
                        performAction("Config")
                    },
                    "Add Device",
                    { d, i ->
                        updateView(sAdd)
                        boardName.value = ""
                    },
                    "Done",
                    { d, i ->
                        addBoardBack()
                    },
                    {

                    })
            }
            else {
                AppDialogs.openTitleDialog(
                    context,
                    getDeviceType(b.thing_type),
                    "${getDeviceType(b.thing_type)} added successfully",
                    "",
                    { d, i ->
                    },
                    "Add Device",
                    { d, i ->
                        updateView(sAdd)
                        boardName.value = ""
                    },
                    "Done",
                    { d, i ->
                        addBoardBack()
                    },
                    {

                    })
            }
        }

    }

    fun selectBoard() {
        selectBoard.value = true
        selectBoard.value = false
    }

    fun createArea() {
        /*createArea.value = true
        createArea.value = false*/
        performAction("createArea")
    }

    fun addBoardBack() {
        addBoardBack.value = true
        addBoardBack.value = false
    }

    private fun display(msg : String) {
        toastMessage.value = msg
        showToast.value = true
        showToast.value = false
    }

}