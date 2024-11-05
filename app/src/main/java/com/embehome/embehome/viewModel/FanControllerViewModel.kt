package com.embehome.embehome.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FanControllerViewModel : ViewModel() {

    val level = MutableLiveData <Boolean>()
    var action = -1

    fun setLevel (data : Int) {
        gotoLevel(data)
    }

    fun gotoLevel (l : Int) {
        action = l
        level.value = true
        level.value = false
    }
}