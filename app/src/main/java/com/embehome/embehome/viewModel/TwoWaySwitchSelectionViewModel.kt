package com.embehome.embehome.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TwoWaySwitchSelectionViewModel : ViewModel() {

    val performOperation = MutableLiveData(false)
    var action = ""


    fun back () {
        performOperation("Back")
    }

    fun cancel () {
        performOperation("Cancel")
    }

    @ExperimentalStdlibApi
    fun save () = viewModelScope.launch{
        performOperation("Save")
        /*if (CacheSceneTwoWay.twoWayPsid != -1 && CacheSceneTwoWay.twoWaySid != -1){
            val data = TwoWaySwitchDetails(
                    CacheSceneTwoWay.twoWayPsid,
                    CacheSceneTwoWay.twoWayPid,
                    CacheSceneTwoWay.twoWaySsid,
                    CacheSceneTwoWay.twoWaySid,
                    CacheSceneTwoWay.twopsno,
                    CacheSceneTwoWay.twossno
            )
            val res = HubFeatureHandleRepository.addTwoWay(CacheHubData.selectedMacID, "192.168.0.4", data)
            if (res) {
                performOperation("Two Way Created Successfully")
                performOperation("Cancel")
            }
            else {
                performOperation("operation failed ${CacheHubData.selectedHubIP}")
            }

        }
        else {
            performOperation("Please Select One Switch From each SSB")
        }*/
    }

    fun performOperation (data : String)  {
        action = data
        performOperation.value = true
        performOperation.value = false
    }

}