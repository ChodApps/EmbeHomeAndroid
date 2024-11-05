package com.embehome.embehome.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TwoWaySSBSelectionViewModel : ViewModel() {

    val performAction = MutableLiveData(false)
    var action = ""


    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    fun back() {
        performAction("Back")
    }

    fun next () {
        performAction("Next")
    }

}