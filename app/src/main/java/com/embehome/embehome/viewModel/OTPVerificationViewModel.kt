package com.embehome.embehome.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HttpManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.ViewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 03-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class OTPVerificationViewModel : ViewModel() {

    val field_1 = MutableLiveData<String>()
    val field_2 = MutableLiveData<String>()
    val field_3 = MutableLiveData<String>()
    val field_4 = MutableLiveData<String>()
    var btnEnable = MutableLiveData<Boolean>()
    var verify = MutableLiveData<Boolean>()
    val email = MutableLiveData<String>()

    val time = MutableLiveData<Int> ()

    val resend = MutableLiveData<Boolean>()

    init {
        btnEnable.value = false
        verify.value = false
        setResendOption()
    }

    fun updateBtn() {
        try {
            Log.d("TronX", getOTP())
            btnEnable.value = getOTP().length == 4
        }
        catch (e : Exception) {
            Log.d("TronX", e.message.toString())
        }
    }

    val toast = MutableLiveData <String> ()


    fun submitData() {
        btnEnable.value = false
        verify.value = true
        Log.d("TronX","onClick OTP Verification ViewModel  ${getOTP()}")
    }

    fun getOTP () = "${field_1.value ?: ""}${field_2.value ?: ""}${field_3.value ?: ""}${field_4.value ?: ""}"


    fun resendOTP () = viewModelScope.launch {
        setResendOption()
        email.value?.let {
            val res = HttpManager.resend(it)
            if (res.status) {
                toast.value = "Resend OTP Successfully"
            }
        }

    }

    private fun setResendOption () = viewModelScope.launch {
        var t = 40
        resend.value = false
        while (t > 0) {
            time.value = t
            delay(1000)
            t -= 1
        }
        resend.value = true
    }

}