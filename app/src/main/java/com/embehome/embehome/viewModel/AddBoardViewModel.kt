package com.embehome.embehome.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Manager.SSBManager
import com.embehome.embehome.Model.AreaData
import com.embehome.embehome.Utils.FakeDB
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 26-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class AddBoardViewModel : ViewModel() {

    val macID = MutableLiveData<String>()
    val addBoardBack = MutableLiveData<Boolean>()
    val addSSB = MutableLiveData<Boolean>()
    val createArea = MutableLiveData<Boolean>()
    val boardName = MutableLiveData<String>()
    val areaName = MutableLiveData<String>()
    val selectBoard = MutableLiveData<Boolean>()
    val showToast = MutableLiveData<Boolean>()
    val toastMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val area = MutableLiveData<AreaData>()

    init {
        areaName.value = "Area Name"
    }

    @ExperimentalStdlibApi
    fun addSSB() {
        addSSB.value = true
        addSSB.value = false
        viewModelScope.launch {
            loading.value = true
            val result = LANManager.addDevice(FakeDB.deviceID.toString().also { FakeDB.deviceID += 1 })
            if (result.status) {
                display("Board added to HUB successfully")
                Log.d("TronX", "Added Board data ${result.data}")
                val res = SSBManager.prepareBoardAddData(
                    result.data,
                    area.value?.area_id ?: 0,
                    boardName.value ?: "new Board",
                    FakeDB.deviceID - 1
                )
                if (res.thing_serial_number != "") {
                    val httpResult = HttpManager.addSwitchBoard(macID.value.toString(), res)
                    if (httpResult.status) {
                        display("Board added to Cloud successfully")
                        addBoardBack()
                    } else {
                        display(
                            httpResult.body as String
                        )
                        display("Reset board and try again")
                    }
                }
            } else
                display(result.data)
            loading.value = false
        }
    }

    fun selectBoard() {
        selectBoard.value = true
        selectBoard.value = false
    }

    fun createArea() {
        createArea.value = true
        createArea.value = false
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