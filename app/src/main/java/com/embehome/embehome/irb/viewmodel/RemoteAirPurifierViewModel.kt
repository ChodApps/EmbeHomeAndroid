package com.embehome.embehome.irb.fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.embehome.embehome.irb.AudioSystemRemoteInfo

class RemoteAirPurifierViewModel : ViewModel() {
    val performAction = MutableLiveData (false)
    var thingsID = 0
    var macID = ""
    var sno = ""
    var remoteID = 0
    val remoteName = MutableLiveData ("TV")
    var action = ""

    val editMode = MutableLiveData(View.GONE)
    var operationType  = ""

    var remoteIRData = getIrData()

    fun getIrData () : HashMap<String,String> {


        return HashMap()
    }

    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    fun back () {
        performAction("Back")
    }

    fun setEditMode (operationType : String) {
        when (operationType) {
            "Create" -> {
                editMode.value = View.VISIBLE
            }

            "Operate" -> {
                editMode.value = View.GONE
            }
        }
    }

    fun submit () {

    }

    var button : AudioSystemRemoteInfo? = null
    fun action (btn : AudioSystemRemoteInfo) {
        if (button == null) {
            button = btn
            performAction ("ClickButton")
        }
    }

}