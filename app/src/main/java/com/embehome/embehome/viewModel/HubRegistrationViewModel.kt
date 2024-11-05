package com.embehome.embehome.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 10-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class HubRegistrationViewModel : ViewModel() {

    val errorWifiPassword = MutableLiveData<Boolean>()
    val isPasswordValid = MutableLiveData<Boolean>()
    var back = MutableLiveData<Boolean>()

    init{
        errorWifiPassword.value = false
        isPasswordValid.value = false
    }

    fun isPasswordValid(value : Boolean) {
        isPasswordValid.value = value
        errorWifiPassword.value = !value
    }

    val wifiSSID = MutableLiveData<String>()

    val wifiPassword = MutableLiveData<String>()

    val _submit = MutableLiveData<Boolean>()

    fun hubRegBack() {
        back.value = true
        back.value = false
    }

    fun submit() {
        if (isPasswordValid.value!!) {
            Log.d("TronX", "Time to Start the ESPTouch ${wifiSSID.value} ${wifiPassword.value}")
            _submit.value = true
            _submit.value = false
        }
    }
}