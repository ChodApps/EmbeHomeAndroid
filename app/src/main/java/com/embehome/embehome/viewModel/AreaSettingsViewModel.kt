package com.embehome.embehome.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Model.AreaData
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Repository.DeviceHandleRepository
import kotlinx.coroutines.launch

class AreaSettingsViewModel : ViewModel() {


    val board = MutableLiveData<BoardDetails>()
    val area = MutableLiveData<AreaData>()
    val isTouchEnabled = MutableLiveData<Boolean>(false)

    val boardName = MutableLiveData("")
    val areaName = MutableLiveData ("")
    val deviceEdit = MutableLiveData(false)



    val performAction = MutableLiveData (false)
    var action = ""

    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    fun edit () {
        performAction ("Edit")
    }

    fun save () {
        performAction ("Save")
    }

    fun displayThingsId () {
        performAction("DisplayID")
    }

    fun changeArea () {
        performAction ("ChangeArea")
    }

    fun back () {
        performAction("Back")
    }

    fun updateBoardData (context: Context,macID : String, b : BoardDetails, oldAreaID : Int) = viewModelScope.launch {
        val res = DeviceHandleRepository.updateDeviceDetails(context,macID, b)
        if (res) {
            board.value?.thing_name = boardName.value.toString()
            if (oldAreaID != area.value?.area_id)
            performAction("Finish")
        }
        board.value = board.value
    }

    fun gotoTouchSensitivity (context: Context) {
        performAction("GoToTouchSensitivity")
    }
}