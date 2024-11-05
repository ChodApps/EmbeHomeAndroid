package com.embehome.embehome.Manager

import android.util.Log
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.LanRequestFeedback
import com.embehome.embehome.Model.SwitchDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.FakeDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/** com.tronx.tt_things_app.Services
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 28-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/



object SSBManager {

    @ExperimentalStdlibApi
    suspend fun prepareBoardAddData (data : String, areaId : Int, boardName : String = "New Board", deviceID : Int) : BoardDetails {
        val CMD = 0
        val STATUS = 1
        val SERIAL_NUMBER = 3
        val DEVICE_ID = 2
        val DEVICE_TYPE = 4
        val SWITCH_DATA = 5
        val VERSION = 6
        var v = "0.0.0"
        val cmdData = LANManager.getTcpData(data).also {
            if ( it.size < 5 || it[CMD] != "BAAB")
                return BoardDetails("","","",0,"","",0,ArrayList<SwitchDetails>())
        }
        if (cmdData.size >= 7) {
            v = cmdData[VERSION]
        }
        AppServices.log("TronX"," Found ${cmdData[DEVICE_TYPE]}")
        return when (cmdData[DEVICE_TYPE]) {
            "SSB", "CUR", "ISB", "RSB" -> {
                val switchList = LANManager.getTcpSwitchData(cmdData[SWITCH_DATA])
                val switchData = ArrayList<SwitchDetails>().apply {
                    switchList.forEachIndexed { index, s ->
                        s.toCharArray().let {
                            try {
                                if (it.isNotEmpty() && it[0] >= 'A' && it[0] <= 'Z') {
                                    this.add(
                                        initializeSwitch(
                                            s,
                                            index + 1
                                        )
                                    )
                                }
                            }
                            catch (e : Exception) {
                                AppServices.log("TronX_Add", " Device Add - ${e.message}")
                                return BoardDetails("","","",0,"","",0,ArrayList<SwitchDetails>())
                            }
                        }
                    }
                }
                 BoardDetails(cmdData[SERIAL_NUMBER], cmdData[DEVICE_TYPE], "c",areaId,boardName,"Image Data",deviceID,switchData, v)
            }
            "IRB" -> {
                 BoardDetails(cmdData[SERIAL_NUMBER], cmdData[DEVICE_TYPE], "c",areaId,boardName,"Image Data",deviceID,ArrayList<SwitchDetails>(), v)
            }


//            "CUR" -> {
//                 BoardDetails(cmdData[SERIAL_NUMBER], cmdData[DEVICE_TYPE], "c",areaId,boardName,"Image Data",deviceID,ArrayList<SwitchDetails>())
//            }
            else -> {
                BoardDetails("","","",0,"","",0,ArrayList<SwitchDetails>(), v)
            }
        }
    }

    private fun initializeSwitch(type : String, id : Int) = SwitchDetails(id,
        when(type) {"A" -> "Switch" "B" -> "Fan" "C" -> "Dimmer" "D" -> "Curtain" "E" -> "Remote"  "F" -> "Bell" else -> "Switch $id"},
        type,
        0,
        0,
        when(type) {"A" -> "switch_icon1" "B" -> "switch_icon2" "C" -> "switch_icon3" "D" -> "switch_icon1" "E" -> "switch_icon8" "F" -> "switch_icon9"  else -> "switch_icon1"},
        ArrayList(),
        null,
        null,
        null
    )


    fun prepareBoardDataToDisplay(data : ArrayList<BoardDetails>) : HashMap<Int, ArrayList<BoardDetails>> {
        val sortedResult = HashMap<Int, ArrayList<BoardDetails>>()

        try {
            data.forEach {
                if (sortedResult[it.area_id] == null) {
                    sortedResult[it.area_id] = ArrayList()
                }
                if (it.thing_id >= FakeDB.deviceID) FakeDB.deviceID = it.thing_id + 1
                sortedResult[it.area_id]?.add(it)
            }
        }
        catch (e : Exception) {
            Log.d("TronX","Failed to parse data as per area id ${e.message}")
        }
        FakeDB.hubBoardData = sortedResult
        return sortedResult
    }


    var isDeleteBoard = false

    @ExperimentalStdlibApi
    suspend fun deleteSSB (serialNumber : String, deviceID: Int) = withContext(Dispatchers.IO) {
        if (isDeleteBoard)
            return@withContext LanRequestFeedback(false,"Previous request is in Process")

        isDeleteBoard  = true
        val tcpResult = LANManager.deleteBoard(deviceID)
        if (!tcpResult.status){
            Log.d("TronX","Board Failed to deleted from Hub")
            return@withContext LanRequestFeedback(false,"Board Failed to deleted from Hub").also { isDeleteBoard = false }
        }

        val httpResult = HttpManager.deleteBoard(serialNumber)
        if (!httpResult.status) {
            Log.d("TronX","Board Deleted frm HUB but Failed to deleted from Cloud")
            return@withContext LanRequestFeedback(false,"Board Deleted frm HUB but Failed to deleted from Cloud ${httpResult.body as String}").also { isDeleteBoard = false }
        }
        LanRequestFeedback(true,"Board Deleted Successfully").also { isDeleteBoard = false
            CacheHubData.deleteBoardData(CacheHubData.selectedMacID, CacheHubData.selectedAreaId, deviceID)
        }
    }

    fun getSSBImage(size : Int, status : Boolean, type: String) : Int {
        return if (type == "SSB") when(size) {
            1 -> R.drawable.tt_device_type_bell

            2 -> if (status) R.drawable.tt_device_enable_switch_2m else R.drawable.tt_device_disable_switch_2m

            4 -> if (status) R.drawable.tt_device_enable_switch_4m else R.drawable.tt_device_disable_switch_4m

            6 -> if (status) R.drawable.tt_device_enable_switch_8m else R.drawable.tt_device_disable_switch_8m

            else -> if (status) R.drawable.tt_device_enable_switch_12m else R.drawable.tt_device_disable_switch_12m
        } else if (type == "ISB") {
            if (status) R.drawable.tt_device_enable_switch_remote else R.drawable.tt_device_disable_switch_remote
        }
        else if (type == "CUR"){
            if (status) R.drawable.tt_device_enable_switch_curtain else R.drawable.tt_device_disable_switch_curtain
        }
        else if (type == "IRB") {
            if (status) R.drawable.tt_bb_enable_remote_mini else R.drawable.tt_bb_disable_remote_mini}
        else {
            if (status) R.drawable.tt_device_enable_switch_remote else R.drawable.tt_device_disable_switch_remote}
    }

}