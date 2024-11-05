package com.embehome.embehome.rules.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RuleSelectSensorViewModel : ViewModel() {
    val conditionDeviceId = MutableLiveData<Int>()
    val conditionDeviceSno = MutableLiveData<String>()
    val conditionSwitchId = MutableLiveData<Int>()
    val conditionSwitchStatus = MutableLiveData<Int>()
}