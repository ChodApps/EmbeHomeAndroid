package com.embehome.embehome.viewModel

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.embehome.embehome.Fragments.RegistrationDirections
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.CloudTermsAndCondition
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 12-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RegistrationViewModel : ViewModel() {

    var regEmailValid = MutableLiveData<Boolean>()
    var regEmail = MutableLiveData<String>()
    var regErrorEmail = MutableLiveData<Boolean>()
    var regMobileValid = MutableLiveData<Boolean>()
    var regMobile = MutableLiveData<String>()
    var regErrorMobile = MutableLiveData<Boolean>()
    var regMobileCounter = MutableLiveData<Boolean>()
    var regPasswordValid = MutableLiveData<Boolean>()
    var regPassword = MutableLiveData<String>()
    var regErrorPassword = MutableLiveData<Boolean>()
    var regCnfPasswordValid = MutableLiveData<Boolean>()
    var regCnfPassword = MutableLiveData<String>()
    var regErrorCnfPassword = MutableLiveData<Boolean>()
    var regTermsAndCond = MutableLiveData<Boolean>()
    var registerBtn = MutableLiveData<Boolean>()
    var register = MutableLiveData<Boolean>()
    var regBack = MutableLiveData<Boolean>()

    init {
        regErrorEmail.value = false
        regErrorMobile.value = false
        regErrorPassword.value = false
        regErrorCnfPassword.value = false
        regMobileCounter.value = false
        register.value = false

        regEmailValid.value = false
        regMobileValid.value = false
        regPasswordValid.value = false
        regCnfPasswordValid.value = false
        regTermsAndCond.value = false
    }

    fun clear () {
        regErrorEmail.value = false
        regErrorMobile.value = false
        regErrorPassword.value = false
        regErrorCnfPassword.value = false
        regMobileCounter.value = false
        register.value = false

        regEmailValid.value = false
        regMobileValid.value = false
        regPasswordValid.value = false
        regCnfPasswordValid.value = false
        regTermsAndCond.value = false
        regEmail.value = null
        regCnfPassword.value = null
        regPassword.value = null
        regMobile.value = null
        regTermsAndCond.value = null
    }

    fun isEmailValid(value : Boolean) {
        regEmailValid.value = value
        regErrorEmail.value = !value
        updateRegisterBtn()
    }

    fun isMobileValid(value : Boolean) {
        regMobileValid.value = value
        regErrorMobile.value = !value
        updateRegisterBtn()
    }

    fun isPasswordValid(value : Boolean) {
        regPasswordValid.value = value
        regErrorPassword.value = !value
        updateRegisterBtn()
    }

    fun isCnfPasswordValid(value : Boolean) {
        regCnfPasswordValid.value = value
        regErrorCnfPassword.value = !value
        updateRegisterBtn()
    }

    fun termAndCondition() {
        updateRegisterBtn()
    }

    private fun updateRegisterBtn () {
        registerBtn.value = (regEmailValid.value == true && regMobileValid.value == true && regPasswordValid.value == true && regCnfPasswordValid.value == true && regTermsAndCond.value == true)

    }



    fun regBack() {
        regBack.value = true
        regBack.value = false
    }

    fun register() {
        registerBtn.value = false
        Log.d("TronX", "Data for API is ${regEmail.value} ${regMobile.value} ${regPassword.value} ${regTermsAndCond.value}")
        register.value = true
        register.value = false
    }

    fun terms (v : View) = viewModelScope.launch{
       termsAndCondition.value?.let {
           val d = RegistrationDirections.actionRegistrationToWebViewDisplay2(it, "Terms and Condition")
           v.findNavController().navigate(d)
       }
    }

    private val termsAndCondition = MutableLiveData<String>()

    fun fetchTerms (context : Context) = viewModelScope.launch {
        val res = HttpManager.getTerms()
        if (res.status) {
            val terms = res.body as CloudTermsAndCondition
            termsAndCondition.value = terms.data.terms_data.terms_url
        }
        else AppServices.toastShort(context, "Unable to fetch Terms and condition")
    }

    fun registerUser (context : Context, controller: NavController) = viewModelScope.launch {
        try {
            AppDialogs.startLoadScreen(context)
            val result =
                HttpManager.userRegister(
                    regEmail.value!!,
                    regMobile.value!!,
                    regPassword.value!!,
                    true
                )

            if (result.status) {
                Log.d("TronX", "User Registration Successful")
                val d = RegistrationDirections.actionRegistrationToOTPVerification( regEmail.value!!)
                controller.navigate(d)
                    .also {
                        AppDialogs.stopLoadScreen()
                    }
            } else {
                Log.d("TronX", "User Registration Failed : ${result.body as String}")
                Toast.makeText(context, "${result.body}", Toast.LENGTH_SHORT).show()
                AppDialogs.stopLoadScreen()
            }
        }
        catch (e : Exception) {

        }
    }
}