package com.embehome.embehome.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HttpManager
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 04-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


@ExperimentalStdlibApi
class LoginViewModel : ViewModel() {


    var registration = MutableLiveData<Boolean>()
    var errorEmail = MutableLiveData<Boolean>()
    var errorPassword = MutableLiveData<Boolean>()
    var isEmailValid = MutableLiveData<Boolean>()
    var isPasswordValid = MutableLiveData<Boolean>()
    var submit = MutableLiveData<Boolean>()

    init {
        registration.value = false
        errorEmail.value = false
        errorPassword.value = false
        isEmailValid.value = false
        isPasswordValid.value = false
        submit.value = false
    }


    var toastString = ""
    var action = ""
    val performAction = MutableLiveData(false)
    fun performAction (data : String, toast : String = "") {
        action = data
        toastString = toast
        performAction.value = true
        performAction.value = false
    }

    var email = MutableLiveData<String>()
    val _email : LiveData<String>
        get() = email

    var paswd = MutableLiveData<String>()
    val _paswd : LiveData<String>
        get() = paswd

    var result = MutableLiveData<String>()





    var btnEnable = MutableLiveData<Boolean>()

    fun isEmailValid() {
        errorEmail.value = !isEmailValid.value!!
        updateLoginBtn()
    }

    fun isPasswordValid() {
        errorPassword.value = !isPasswordValid.value!!
        updateLoginBtn()
    }

    private fun updateLoginBtn() {
        btnEnable.value = ((isEmailValid.value ?: false) && (isPasswordValid.value ?: false))
    }

    fun register() {
        registration.value = true
        registration.value = false
    }

    fun submit() {
        btnEnable.value = false
        Log.d("TronX","Login Submit Btn invoked in ViewModel ${_email.value} isPassword Valid = ${errorPassword.value}")
        submit.value = true
        submit.value = false
    }

    fun forgotPassword() {
        Log.d("TronX","forgot password, pending Implementation")
        performAction("ForgotPassword")
    }

    fun requestForgotPassword (context: Context, email :String) = viewModelScope.launch {
        val res = HttpManager.forgotPassword(email)
        res.status.also {
            if (it) {
                performAction("reset","Password reset link has sent to your email, please reset the password using the link")
            }
            else performAction("Toast",res.body as String)
        }
    }

}