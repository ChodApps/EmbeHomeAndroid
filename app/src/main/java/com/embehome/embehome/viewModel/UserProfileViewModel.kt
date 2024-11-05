package com.embehome.embehome.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Activity.MainActivity
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.HttpErrorModel
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 25-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class UserProfileViewModel : ViewModel() {

    val deleteUserAlert = MutableLiveData<Boolean>()
    val userProfileBack = MutableLiveData<Boolean>()
    val logOut = MutableLiveData<Boolean>()
    val changePassword = MutableLiveData<Boolean>()
    val deleteUser = MutableLiveData<Boolean>()

    fun changePassword() {
        Log.d("TronX","Change Password Initiated")
        changePassword.value = true
        changePassword.value = false
    }
    fun deleteUserAlert() {
        Log.d("TronX","Logging Out")
        deleteUserAlert.value = true
        deleteUserAlert.value = false
    }
    fun logOut() {
        Log.d("TronX","Logging Out")
        logOut.value = true
        logOut.value = false
    }
    fun deleteUser() {
        Log.d("TronX","Logging Out")
        deleteUser.value = true
        deleteUser.value = false
    }
    fun back() {
        Log.d("TronX", "Going to Previous Screen")
        userProfileBack.value = true
        userProfileBack.value = false
    }

    fun contactUs() {
        performAction("ContactUs")
    }

    fun aboutUs () {
        performAction("AboutUs")
    }

    fun notificationPref () {
        performAction("Notification")
    }

    var action : String = ""
    val performAction = MutableLiveData(false)
    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }
    suspend fun deleteUserAlert(context : Activity){
        logout(context)
    }
    fun logout (context : Activity) = viewModelScope.launch {
        Log.d("TronX","removeUserDetails")
        AppDialogs.startLoadScreen(context)
        val token = AppServices.getToken(context, "fcm")
        if (token.length > 5) {
            val res = HttpManager.logout(token)
            if (res.status) {
                AppServices.toastShort(context, "Log out Successful")
                context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit().clear().apply()
                CacheHubData.deleteAll()
                context.finish()
                context.startActivity(Intent(context, MainActivity::class.java))
            }
            else {
                try {
                    val err = res.body as HttpErrorModel
                    if (err.error_code == 400030L) {
                        AppServices.toastShort(context, "Log out Successful")
                        context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit().clear().apply()
                        CacheHubData.deleteAll()
                        context.finish()
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
                }
                catch (e : Exception) {
                    AppServices.toastShort(context, "Log out Successful")
                    context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit().clear().apply()
                    CacheHubData.deleteAll()
                    context.finish()
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
            }
        } else {
            AppServices.toastShort(context, "Log out Successful")
            context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit().clear().apply()
            CacheHubData.deleteAll()
            context.finish()
            context.startActivity(Intent(context, MainActivity::class.java))
        }
        AppDialogs.stopLoadScreen()
    }
    fun deleteUser (context : Activity) = viewModelScope.launch {
        Log.d("TronX","removeUserDetails")
        AppDialogs.startLoadScreen(context)
        val userID = AppServices.getToken(context,"email")//AppServices.getToken(context, "fcm")
        Log.e("user Id", userID)
        if (userID.length > 4) {
            val res = HttpManager.deleteUser(userID)
            if (res.status) {
                deleteUserAlert()
//                AppServices.toastLong(context, "Your request to delete the user has been received. It will be reviewed and processed by the administrator")
//                logOut()
                Log.e("res delete user", (res.body?:"").toString())
//                context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit().clear().apply()
//                CacheHubData.deleteAll()
//                context.finish()
//                context.startActivity(Intent(context, MainActivity::class.java))
            }
            else {
                try {
                    val err = res.body as HttpErrorModel
                    if (err.error_code == 400030L) {
                        AppServices.toastShort(context, "Log out Successful")
                        context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit().clear().apply()
                        CacheHubData.deleteAll()
                        context.finish()
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
                }
                catch (e : Exception) {
                    AppServices.toastShort(context, "Log out Successful")
                    context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit().clear().apply()
                    CacheHubData.deleteAll()
                    context.finish()
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
            }
        } else {
            AppServices.toastShort(context, "Log out Successful")
            context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit().clear().apply()
            CacheHubData.deleteAll()
            context.finish()
            context.startActivity(Intent(context, MainActivity::class.java))
        }
        AppDialogs.stopLoadScreen()
    }

}