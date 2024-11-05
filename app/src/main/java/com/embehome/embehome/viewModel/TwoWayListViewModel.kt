package com.embehome.embehome.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay

class TwoWayListViewModel : ViewModel() {

    val performAction = MutableLiveData(false)
    var action = ""
    val twoWayData = CacheSceneTwoWay.getTwoWayData(CacheHubData.selectedMacID)


    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    fun back () {
        performAction("Back")
    }

    fun add () {
        performAction("Add")
    }

}