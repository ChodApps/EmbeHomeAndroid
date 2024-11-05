package com.embehome.embehome.irb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.coroutines.launch

class ListRemoteViewModel : ViewModel() {

    val tid = MutableLiveData<Int>()
    var action = ""
    val performAction = MutableLiveData<Boolean>()
    fun performAction (d : String) {
        action = d
        performAction.value = true
        performAction.value = false
    }

    val viewAllRemote = MutableLiveData(false)

    val irbRemoteData : MutableLiveData<HashMap<String, ArrayList<RemoteCloudModel>>> = MutableLiveData(HashMap())
    val allRemotes = ArrayList<RemoteCloudModel> ()

    val irbList = CacheHubData.getAllIRBList(CacheHubData.selectedMacID).also {
        viewModelScope.launch {
            it.forEach {ssb ->
                CacheRemoteData.getIRBRemoteList(CacheHubData.selectedMacID, ssb.thing_serial_number).also {
                    /*if (it.size != 0) {*/
                    addIrbData(ssb.thing_serial_number, it)
//                    }
                }
            }
        }
    }

    fun allRemotes (id : Int) {
        CacheHubData.getAllIRBList(CacheHubData.selectedMacID).also {
            viewModelScope.launch {
                allRemotes.clear()
                it.forEach { ssb ->
                    if (ssb.thing_id != id)
                        CacheRemoteData.getIRBRemoteList(
                            CacheHubData.selectedMacID,
                            ssb.thing_serial_number
                        ).also { a ->
                            allRemotes.addAll(a)
                        }
                }
                performAction("refreshAdapter")
            }
        }
    }

    fun addIrbData (sno : String, data : ArrayList<RemoteCloudModel>) {
        if (irbRemoteData.value!![sno] != null) {
            irbRemoteData.value!![sno]?.clear()
            irbRemoteData.value!![sno]?.addAll(data)
        }
        irbRemoteData.value!![sno] = data.also {
            performAction("refreshAdapter")
        }
    }

}