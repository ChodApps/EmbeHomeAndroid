package com.embehome.embehome.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.CloudAPIRequestReceiver
import kotlinx.coroutines.launch

class SetNotificationAlertViewModel : ViewModel(){

    val create = "Create Alert"
    val delete = "Delete"
    var board : BoardDetails? = null
    val switchName = MutableLiveData<String>()
    val repeat = MutableLiveData<Boolean>(false)
    val switchStatus = MutableLiveData(false)
    val hour = MutableLiveData(0)
    val min = MutableLiveData(0)
    val createMode = MutableLiveData(false)
    val btnText = MutableLiveData("")

    var macID = ""
    var thingsID = -1
    var switchId = -1
    var sno = ""

    val performAction = MutableLiveData(false)
    var action : SetAlert = SetAlert.Toast
    var toastString = ""

    init {

    }

    fun performAction (data : SetAlert) {
        action = data
        performAction.value = true
        performAction.value =false
    }

    fun createAlert () = viewModelScope.launch {
        if (btnText.value == create) {
            try {
                if (hour.value!! != 0 || min.value!! != 0) {
                    val timeInterval = (hour.value ?: 0) * 60 + min.value!!
                    Log.d("TronX", "$macID $thingsID $switchId ${switchStatus.value} $timeInterval min ${repeat.value}")
                    val res = HttpManager.setAlert(macID, thingsID,sno, switchId, if (switchStatus.value!!) 1 else 0, timeInterval, "", if (repeat.value!!) 1 else 0)
                    if (res.status) {
                        val data = res.body as CloudAPIRequestReceiver
                        toastString = data.message
                        performAction (SetAlert.Toast)
                        back()
                    }
                    else {
                        toastString = res.body.toString()
                        performAction (SetAlert.Toast)
                    }
                }
                else {
                    toastString = "Please Select at least 1 min"
                    performAction (SetAlert.Toast)
                }
            }
            catch (e : Exception){}
        }
        else if (btnText.value == delete) {
            try {
                val id = board?.switchesList!![switchId].alert_data?.alert_id ?: 0
                val res = HttpManager.deleteAlert(id)
                if (res.status) {
                    val data = res.body as CloudAPIRequestReceiver
                    toastString = data.message
                    performAction (SetAlert.Toast)
                    board?.switchesList!![switchId].alert_data = null
                    back()
                }
                else {
                    toastString = res.body.toString()
                    performAction (SetAlert.Toast)
                }
            }
            catch(e : Exception) {

            }
        }
    }

    fun back () {
        performAction (SetAlert.Back)
    }

}

enum class SetAlert {
    Toast,
    Back,

}
