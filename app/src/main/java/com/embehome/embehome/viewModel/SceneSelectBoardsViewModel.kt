package com.embehome.embehome.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SceneSelectBoardsViewModel : ViewModel() {

    val performAction = MutableLiveData(false)
    var action = ""
    val expanded = MutableLiveData (-1)
    val selectedBoard = MutableLiveData<ArrayList<Int>>(ArrayList())

    fun gotoSwitches() {
        performAction("Next")
    }


    fun sceneBack() {
        performAction("Back")
    }

    fun performAction(data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

}