package com.embehome.embehome.irb.viewmodel

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.irb.AcRemoteInfo
import com.embehome.embehome.irb.RemoteButtons
import com.embehome.embehome.irb.RemoteCategoryEnum
import com.embehome.embehome.irb.RemoteOperations
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.irb.viewmodel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 18-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RemoteCmnViewModel : ViewModel() {

    val irb = MutableLiveData<BoardDetails>()
    var macID = ""
    var remoteID = 0
    var remoteCategory = MutableLiveData<RemoteCategoryEnum>()
    val remoteName = MutableLiveData ("TV")
    val editMode = MutableLiveData(View.VISIBLE)
    var operationType  = ""
    var remoteIRData = getIrData()
    var rThingsID = -1

    fun getIrData () : HashMap<String,String> {
        return HashMap()
    }

    val performAction = MutableLiveData (false)
    var action = ""
    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    fun back () {
        performAction("Back")
    }

    var button : RemoteButtons? = null
    fun action (btn : RemoteButtons) {

        if (button == null) {
            button = btn
            performAction ("ClickButton")
        }
    }

    fun openMenu () {

    }

    @ExperimentalStdlibApi
    fun action (context: Context, btnName : String, irValue : String, operationType: String, thingsID : Int) = viewModelScope.launch {
        val res =  RemoteOperations.operateRemote(context, btnName, irValue, operationType, thingsID)

        if (operationType != "Operate" && (res != "Failed" && res != "nil")) {
            remoteIRData[btnName] = res
        }
        else if (operationType != "Operate" && res == "Failed") {
            AppServices.toastShort(context, "Remote Button Read Failed")
        }
        acReadMode.value = false
        button = null
    }


    fun saveRemote () {
      //  performAction(" Saving Remote")

        if (operationType == "Edit")
        performAction ("Update")
        else performAction("Save")
    }

    fun saveRemote (context: Context, macID : String, irb : BoardDetails, remoteName : String, remoteID : Int, category : String, irData : HashMap<String, String>) = viewModelScope.launch{
        if (irData.size <= 0) {
            AppServices.toastShort(context, "Please Read At least one value to save the remote")

        }
        else if (operationType == "Edit") {
            val res = RemoteOperations.updateRemote(
                context,
                macID,
                irb.thing_serial_number,
                irb.thing_id,
                remoteName,
                remoteID,
                category,
                irData
            )
            if (res)
                performAction("Exit")
        }
        else{
            val res = RemoteOperations.saveRemote(
                context,
                macID,
                irb.thing_serial_number,
                irb.thing_id,
                remoteName,
                remoteID,
                category,
                irData
            )
            if (res)
                performAction("Exit")
        }
    }

    fun delete (context: Context, macID: String, serialNo : String, remoteID: Int, name : String) = viewModelScope.launch {
        val res = RemoteOperations.deleteRemote(context, macID, serialNo, remoteID, name)
        if (res)
            back()
    }


    //////////////////////////////////////////////////// AC Remote ////////////////////////////////

    private val fanOff = 0
    private val fanLow = 1
    private val fanMed = 2
    private val fanHigh = 3
    private val vuSwing = "UP"
    private val vmSwing = "MIDDLE"
    private val vdSwing = "DOWN"
    val autoMode = MutableLiveData<Boolean> (true)
    val coolMode = MutableLiveData <Boolean> (false)
    val dryMode = MutableLiveData<Boolean> (false)
    val heatMode = MutableLiveData <Boolean> (false)
    val fanLevel = MutableLiveData<Int> (fanOff)
    val acMode = MutableLiveData<String> ("AUTO")
    val acPower = MutableLiveData<Boolean> (false)
    val acTemp = MutableLiveData<String> ("--")
    val acReadMode = MutableLiveData <Boolean> (false)
    val swingVertical = MutableLiveData <String> (vdSwing)
    val swingHorizontal = MutableLiveData<Boolean> (false)
    val swingHorizontalStatus = MutableLiveData<String>("OFF")

    fun remoteACAction (btn : AcRemoteInfo) {
        try {
            if (acReadMode.value == false) {
                if (acPower.value == true) {
                    when (btn) {
                        AcRemoteInfo.MODE -> {
                            acMode.value = when (acMode.value) {
                                "AUTO" -> {
                                    coolMode.value = true
                                    dryMode.value = false
                                    heatMode.value = false
                                    autoMode.value = false
                                    "COOL"
                                }
                                "COOL" -> {
                                    coolMode.value = false
                                    dryMode.value = true
                                    heatMode.value = false
                                    autoMode.value = false
                                    "DRY"
                                }
                                "DRY" -> {
                                    coolMode.value = false
                                    dryMode.value = false
                                    heatMode.value = true
                                    autoMode.value = false
                                    "HEAT"
                                }
                                else -> {
                                    coolMode.value = false
                                    dryMode.value = false
                                    heatMode.value = false
                                    autoMode.value = true
                                    "AUTO"
                                }
                            }
                            if (operationType == "Operate") {
                                acMode.value?.let {
                                    action(RemoteButtons.valueOf("KEY_MODE_$it"))
                                }
                            }
                        }
                        AcRemoteInfo.AUTO -> {
                            coolMode.value = false
                            dryMode.value = false
                            heatMode.value = false
                            autoMode.value = true
                            acMode.value = "AUTO"
                            if (operationType == "Operate") {
                                acMode.value?.let {
                                    action(RemoteButtons.KEY_MODE_AUTO)
                                }
                            }
                        }
                        AcRemoteInfo.FAN -> {
                            setFan()
                            if (operationType == "Operate") {
                                fanLevel.value?.let {
                                    when (it) {
                                        fanLow -> {
                                            action(RemoteButtons.KEY_FAN_LOW)
                                        }
                                        fanOff -> {
                                            action(RemoteButtons.KEY_FAN_OFF)
                                        }
                                        fanMed -> {
                                            action(RemoteButtons.KEY_FAN_MEDIUM)
                                        }
                                        fanHigh -> {
                                            action(RemoteButtons.KEY_FAN_HIGH)
                                        }
                                    }
                                }
                            }
                        }
                        AcRemoteInfo.RESET -> {
                            if (operationType == "Operate") {
                                action(RemoteButtons.KEY_RESET)
                            }
                        }
                        AcRemoteInfo.CLEAN -> {
                            if (operationType == "Operate") {
                                action(RemoteButtons.KEY_CLEAN)
                            }
                        }
                        AcRemoteInfo.SWING_H -> {
                            if (swingHorizontalStatus.value == "OFF") {
                                swingHorizontalStatus.value = "ON"
                            } else {
                                swingHorizontalStatus.value = "OFF"
                            }
                            if (operationType == "Operate") {
                                if (swingHorizontalStatus.value == " OFF") {
                                    action(RemoteButtons.KEY_SWING_OFF)
                                } else action(RemoteButtons.KEY_SWING_ON)
                            }
                        }
                        AcRemoteInfo.SWING_V -> {
                            swingVertical.value = when (swingVertical.value) {
                                vuSwing -> {
                                    vmSwing
                                }
                                vmSwing -> {
                                    vdSwing
                                }
                                vdSwing -> {
                                    vuSwing
                                }
                                else -> {
                                    vuSwing
                                }
                            }
                            if (operationType == "Operate") {
                                when (swingVertical.value) {
                                    vuSwing -> {
                                        action(RemoteButtons.KEY_VERTICAL_SWING_UP)
                                    }
                                    vdSwing -> {
                                        action(RemoteButtons.KEY_VERTICAL_SWING_DOWN)
                                    }
                                    vmSwing -> {
                                        action(RemoteButtons.KEY_VERTICAL_SWING_MID)
                                    }
                                }
                            }
                        }
                        AcRemoteInfo.POWER -> {
                            acPower.value = false
                            acTemp.value = "--"
                            if (operationType == "Operate") {
                                action(RemoteButtons.KEY_POWER_OFF)
                            }
                        }
                        AcRemoteInfo.UP -> {
                            acTemp.value?.let {
                                try {
                                    it.toInt().let {
                                        if (it < 30) {
                                            acTemp.value = (it + 1).toString()
                                            if (operationType == "Operate") {
                                                if (acPower.value == true)
                                                    action(RemoteButtons.valueOf("KEY_${acMode.value ?: "AUTO"}_${acTemp.value?.toInt() ?: 17}"))
                                            }
                                        }
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        }
                        AcRemoteInfo.DOWN -> {
                            acTemp.value?.let {
                                try {
                                    it.toInt().let {
                                        if (it > 17) {
                                            acTemp.value = (it - 1).toString()
                                            if (operationType == "Operate") {
                                                if (acPower.value == true)
                                                    action(RemoteButtons.valueOf("KEY_${acMode.value ?: "AUTO"}_${acTemp.value?.toInt() ?: 17}"))
                                            }
                                        }
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        }
                    }
                } else if (btn == AcRemoteInfo.POWER) {
                    acPower.value = true
                    acTemp.value = "25"
                    if (operationType == "Operate") {
                        action(RemoteButtons.KEY_POWER_ON)
                    }
                }
            } else {
                when (btn) {
                    AcRemoteInfo.POWER -> {
                        if (acPower.value == true) {
                            action(RemoteButtons.KEY_POWER_ON)
                        } else {
                            action(RemoteButtons.KEY_POWER_OFF)
                        }
                    }
                    AcRemoteInfo.MODE -> {
                        acMode.value?.let {
                            action(RemoteButtons.valueOf("KEY_MODE_$it"))
                        }
                    }
                    AcRemoteInfo.AUTO -> {
                        action(RemoteButtons.KEY_MODE_AUTO)
                    }
                    AcRemoteInfo.FAN -> {
                        fanLevel.value?.let {
                            when (it) {
                                fanLow -> {
                                    action(RemoteButtons.KEY_FAN_LOW)
                                }
                                fanOff -> {
                                    action(RemoteButtons.KEY_FAN_OFF)
                                }
                                fanMed -> {
                                    action(RemoteButtons.KEY_FAN_MEDIUM)
                                }
                                fanHigh -> {
                                    action(RemoteButtons.KEY_FAN_HIGH)
                                }
                            }
                        }
                    }
                    AcRemoteInfo.RESET -> {
                        action(RemoteButtons.KEY_RESET)
                    }
                    AcRemoteInfo.CLEAN -> {
                        action(RemoteButtons.KEY_CLEAN)
                    }
                    AcRemoteInfo.SWING_H -> {
                        if (swingHorizontalStatus.value == " OFF") {
                            action(RemoteButtons.KEY_SWING_OFF)
                        } else action(RemoteButtons.KEY_SWING_ON)
                    }
                    AcRemoteInfo.SWING_V -> {
                        when (swingVertical.value) {
                            vuSwing -> {
                                action(RemoteButtons.KEY_VERTICAL_SWING_UP)
                            }
                            vdSwing -> {
                                action(RemoteButtons.KEY_VERTICAL_SWING_DOWN)
                            }
                            vmSwing -> {
                                action(RemoteButtons.KEY_VERTICAL_SWING_MID)
                            }
                        }

                    }
                    AcRemoteInfo.UP -> {
                        if (acPower.value == true)
                            action(RemoteButtons.valueOf("KEY_${acMode.value ?: "AUTO"}_${acTemp.value?.toInt() ?: 17}"))
                    }
                    AcRemoteInfo.DOWN -> {
                        if (acPower.value == true)
                            action(RemoteButtons.valueOf("KEY_${acMode.value ?: "AUTO"}_${acTemp.value?.toInt() ?: 17}"))
                    }
                }
//            acReadMode.value = false
            }
        }
        catch (e : Exception) {
            AppServices.log("TronX _ AC", e.message.toString())
        }
    }


    fun acRead () {
        acReadMode.value = true
    }

    fun setMode () {

    }

    fun setFanImage (context : Context, img : ImageView, level : Int) {
        img.setImageResource(when (level) {
            fanOff -> {
                R.drawable.tt_device_disable_fan
            }
            fanLow -> {
                R.drawable.tt_device_enable_fan
            }
            fanMed -> {
                R.drawable.tt_cmn_enable_fan_4
            }
            fanHigh -> {
                R.drawable.tt_cmn_enable_fan_5
            }
            else -> {
                R.drawable.tt_device_disable_fan
            }
        })
    }

    private fun setFan () {
        fanLevel.value = when (fanLevel.value) {
            fanOff -> {
                fanLow
            }
            fanLow -> {
                fanMed
            }
            fanMed -> {
                fanHigh
            }
            fanHigh -> {
                fanOff
            }
            else -> {
                fanOff
            }
        }
    }

}