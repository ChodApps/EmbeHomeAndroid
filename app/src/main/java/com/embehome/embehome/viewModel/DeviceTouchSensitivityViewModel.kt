package com.embehome.embehome.viewModel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Repository.DeviceHandleRepository
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.coroutines.launch


/** com.tronx.things.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 15-12-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class DeviceTouchSensitivityViewModel : ViewModel() {

    val touchDisable = MutableLiveData(false)
    val touchFeather = MutableLiveData(false)
    val touchMild = MutableLiveData(false)
    val touchHard = MutableLiveData(false)
    var touch = -1

    fun enableDisableTouch () {
        touchDisable.value = true
        touchFeather.value = false
        touchHard.value = false
        touchMild.value = false
        touch = 0
    }

    fun enableFeatherTouch () {
        touchDisable.value = false
        touchFeather.value = true
        touchHard.value = false
        touchMild.value = false
        touch = 1
    }

    fun enableMildTouch () {
        touchDisable.value = false
        touchFeather.value = false
        touchHard.value = false
        touchMild.value = true
        touch = 2
    }

    fun enableHardTouch () {
        touchDisable.value = false
        touchFeather.value = false
        touchHard.value = true
        touchMild.value = false
        touch = 3
    }

    fun disableAll () {
        touchDisable.value = false
        touchFeather.value = false
        touchHard.value = false
        touchMild.value = false
        touch = -1
    }

    fun enableSensitivity (level : Int) {
        when (level) {
            0 -> {
                enableDisableTouch()
            }
            1-> {
                enableFeatherTouch()
            }
            2 -> {
                enableMildTouch()
            }
            3 -> {
                enableHardTouch()
            }
            else -> {
                disableAll()
            }
        }

    }

    @ExperimentalStdlibApi
    fun getTouchSensitivityLevel (context: Context, ip : String, b : BoardDetails) = viewModelScope.launch {
        AppDialogs.startLoadScreen(context)
        val res = LANManager.getTouchSensitivityLevel(ip, b.thing_id)
        if (res.status) {
            try {
                val touch = Character.getNumericValue(res.data[res.data.length - 1])
                enableSensitivity(touch)
            }
            catch (e : Exception) {
               // disableAll()
            }
        }
        else
        disableAll()
        AppDialogs.stopLoadScreen()
    }

    @ExperimentalStdlibApi
    fun setTouchSensitivityLevel (activity : Activity, context: Context, ip: String, b : BoardDetails, touchLevel : Int)  = viewModelScope.launch {
        AppDialogs.startLoadScreen(context)
        val res = LANManager.setTouchSensitivityLevel(ip, b.thing_id, touchLevel)
        if (res.status) {
            AppServices.toastShort(context, "Touch sensitivity changed and updating in cloud")
            updateBoardData(
                activity,
                context,
                CacheHubData.selectedMacID,
                BoardDetails(b.thing_serial_number, b.thing_type, b.category, b.area_id, b.thing_name, b.thing_image, b.thing_id, b.switchesList, b.thing_version, touchLevel.toString())
            )
            /*AppServices.toastShort(context, "Touch sensitivity updated successfully")
            AppDialogs.stopLoadScreen()
            activity.onBackPressed()*/
        }
        else {
            AppServices.toastShort(context, "Unable to connect to Hub")
            AppDialogs.stopLoadScreen()
        }
    }

    private fun updateBoardData (activity: Activity, context: Context, macID : String, b : BoardDetails) = viewModelScope.launch {
        val res = DeviceHandleRepository.updateDeviceDetails(context,macID, b)
        AppDialogs.stopLoadScreen()
        if (res) {
//            AppServices.toastShort(context, "Touch sensitivity updated successfully")
            activity.onBackPressed()
        }
        else
            AppServices.toastShort(context, "Touch sensitivity updated successfully, Unable to connect to cloud.")
    }

}