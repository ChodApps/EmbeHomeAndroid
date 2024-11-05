package com.embehome.embehome.irb.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.irb.RemoteCategoryEnum

class RemoteCategoryViewModel : ViewModel() {

    val remoteName = MutableLiveData<String>()
    val irbName = MutableLiveData("IRB Not Found")
    val irbSelected = MutableLiveData<BoardDetails>()
    val irbList = CacheHubData.getAllIRBListAddRemote(CacheHubData.selectedMacID).also {
        Log.d("TronX",it.toString())
        if (it != null && it.isNotEmpty()) {
            irbSelected.value = it[0]
            irbName.value = irbSelected.value!!.thing_name
        }
    }

    val irbCategory = MutableLiveData<RemoteCategoryEnum>()

    val actionTrigger = MutableLiveData(false)
    var action = ""
    var toastText = ""

    fun back () {
        performAction("Back")
    }


    fun performAction (operation : String) {
        action = operation
        actionTrigger.value = true
        actionTrigger.value = false
    }

    fun selectIRB () {
        performAction("selectIRB")
    }

    fun submit () {
         if (irbCategory.value != null && irbSelected.value != null && remoteName.value != null) {
//             toastText = "Remote success ***${remoteName.value}*** --- ***${irbCategory.value}*** --- ***${irbSelected.value!!.thing_name}***"
//             performAction("Toast")
             performAction("Continue")
         }
    }

}