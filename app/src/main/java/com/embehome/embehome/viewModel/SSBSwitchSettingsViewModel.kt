package com.embehome.embehome.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.coroutines.launch

class SSBSwitchSettingsViewModel : ViewModel() {

    private val save = "SAVE"
    private val edit = "EDIT"
    val create = "CREATE ALERT"
    val delete = "DELETE ALERT"

    val board = MutableLiveData<BoardDetails>()
    val switchName = MutableLiveData<String>()
    val powerNumber = MutableLiveData<String>()

    val alertRepeat = MutableLiveData(false)
    val alertStatus = MutableLiveData(false)
    val alertTime = MutableLiveData("")
    var alertID = -1
    val alert = MutableLiveData(false)
    val alertBtn = MutableLiveData("")
    val hour = MutableLiveData(0)
    val min = MutableLiveData(0)
    var macID = CacheHubData.selectedMacID
    var thingsID = 0
    var sno = ""
    var switchID = 0

    val firstTime = MutableLiveData(true)

    val performAction = MutableLiveData(false)
    var action = ""
    var toastString = ""

    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    fun toast (data : String) {
        toastString = data
        action = "Toast"
        performAction.value = true
        performAction.value = false
    }

    fun back() {
        performAction("Back")
    }

    val switchDetailButton = MutableLiveData(edit)
    val editMode = MutableLiveData (false)
    fun switchDetailButton () {

        if (switchDetailButton.value == save) {
            if (!switchName.value.isNullOrEmpty() && !powerNumber.value.isNullOrEmpty())
            performAction("removeFocus")
        }

        if (switchDetailButton.value == edit) {
            editMode.value = editMode.value?.not().also {
                switchDetailButton.value = if (it == true) "SAVE" else "EDIT"
            }
        }
    }

    fun updateSwitchDetails (sno : String, switchID: Int, switchName : String, switchType : String, switchIcon : String, watt : Int) = viewModelScope.launch {
        val res = HttpManager.updateSwitchDetails(
            sno,
            switchID,
            switchName,
            switchType,
            switchIcon,
            watt
        )
        if (res.status) {
            val switch = board.value?.switchesList?.filter { it.switchId == switchID }!![0]
            switch.switchName = switchName
            switch.switchWattage = watt
            switch.switchIconId = switchIcon
            toast("Switch Details Updated Successfully")
        }
        else {
            board.value = board.value
            toast("Switch Details Updated Failed")
        }
        editMode.value = editMode.value?.not().also {
            switchDetailButton.value = if (it == true) "SAVE" else "EDIT"
        }
    }

    fun createAlert () = viewModelScope.launch {
        if (alertBtn.value == create) {
            try {
                if (hour.value!! != 0 || min.value!! != 0) {
                    val timeInterval = (hour.value ?: 0) * 60 + min.value!!
                    Log.d("TronX", "$macID $sno $switchID ${alertStatus.value} $timeInterval min ${alertRepeat.value}")
                    val res = HttpManager.setAlert(macID, thingsID, sno, switchID, if (alertStatus.value!!) 1 else 0, timeInterval, "", if (alertRepeat.value!!) 1 else 0)
                    if (res.status) {
                        back()
                    }
                    else {
                        toastString = res.body.toString()
                        performAction ("Toast")
                    }
                }
                else {
                    toastString = "Please Select at least 1 min"
                    performAction ("Toast")
                }
            }
            catch (e : Exception){

            }
        }
        else if (alertBtn.value == delete) {
            try {
                val id =  alertID
                val res = HttpManager.deleteAlert(id)
                if (res.status) {



                    performAction("DeleteAlert")
                    back()
                }
                else {
                    toastString = res.body.toString()
                    performAction ("Toast")
                }
            }
            catch(e : Exception) {

            }
        }
    }

}