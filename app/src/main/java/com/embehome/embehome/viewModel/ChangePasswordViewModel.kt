package com.embehome.embehome.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 25-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class ChangePasswordViewModel : ViewModel() {

    val oldPassword = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    val cnfPassword = MutableLiveData<String>()
    val saveBtnEnabled = MutableLiveData<Boolean>()
    val changePasswordBack = MutableLiveData<Boolean>()
    val changePassword = MutableLiveData<Boolean>()
    var regPasswordValid = MutableLiveData<Boolean>()
    var regCnfPasswordValid = MutableLiveData<Boolean>()
    var regErrorPassword = MutableLiveData<Boolean>()
    var regErrorCnfPassword = MutableLiveData<Boolean>()
    var registerBtn = MutableLiveData<Boolean>(false)


    init {
        saveBtnEnabled.value = false
    }

    fun isPasswordValid(value : Boolean) {
        regPasswordValid.value = value
        regErrorPassword.value = !value
        validatePasswords()
    }

    fun isCnfPasswordValid(value : Boolean) {
        regCnfPasswordValid.value = value
        regErrorCnfPassword.value = !value
        validatePasswords()
    }

    fun changePasswordBack() {
        changePasswordBack.value = true
        changePasswordBack.value = false
    }

    fun changePassword() {
        changePassword.value = true
        changePassword.value = false
    }

    fun validatePasswords () : Boolean {
        return (oldPassword.value?.length ?: 0 > 7 && regPasswordValid.value == true && regCnfPasswordValid.value == true).also {
            registerBtn.value = it
        }
    }

}