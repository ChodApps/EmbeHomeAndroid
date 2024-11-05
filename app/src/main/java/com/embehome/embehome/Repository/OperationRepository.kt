package com.embehome.embehome.Repository

import android.content.Context
import android.util.Log
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Services.MqttClient
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.notificationReceived
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.Repository
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 18-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object OperationRepository {

    @ExperimentalStdlibApi
    fun performSwitchOperation (context: Context, macID: String, areaID: Int, thingsID: Int, switchID: Int, switchStatus: Int, hubIp: String = "") {
        if (hubIp.isNotEmpty()) {
            GlobalScope.launch (Main) {
                val result = LANManager.performSwitchAction(thingsID, switchID, switchStatus,hubIp)
                if (result.status) {
                   val res = LANManager.getTcpData(result.data)
                    if (res.size == 4 && res[0] == "CAAA" && res[1].toInt() == thingsID) {
                        CacheHubData.switchDataUpdate(macID, areaID, thingsID, LANManager.getTcpSwitchData(res[2]))
                    }
                }
                else{
//                    Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                    MqttClient.publishOnMqtt(context, macID, LANManager.getSwitchCmd(thingsID, switchID, switchStatus))
                }
            }
        }
        else {
//            Toast.makeText(context, "Hub Not In Local Network Implement MQtt", Toast.LENGTH_SHORT).show()
            // Implement MQTT here
            MqttClient.publishOnMqtt(context, macID, LANManager.getSwitchCmd(thingsID, switchID, switchStatus))
        }
    }

    @ExperimentalStdlibApi
    fun performSwitchSleepOperation (context: Context, macID: String, areaID: Int, thingsID: Int, status : Int, hubIp: String = "") {
        if (hubIp.length > 1) {
            GlobalScope.launch (Main) {
                val result = LANManager.performSleepAction(thingsID, status, hubIp)
                if (result.status) {
                    Log.e("TronX_Sleep","Sleep Operation response - ${result.data}")
                    val res = LANManager.getTcpData(result.data)
                    if (res.size == 4 && res[0] == "CAAA" && res[1].toInt() == thingsID) {
                        CacheHubData.switchDataUpdate(macID, areaID, thingsID, LANManager.getTcpSwitchData(res[2]))
                    }
                }
                else
                    MqttClient.publishOnMqtt(context, macID, LANManager.getSleepCmd(thingsID, status))
                //Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
            }
        }
        else {
//            Toast.makeText(context, "Hub Not In Local Network Implement MQtt", Toast.LENGTH_SHORT).show()
            // Implement MQTT here
            MqttClient.publishOnMqtt(context, macID, LANManager.getSleepCmd(thingsID, status))
        }
    }

    @ExperimentalStdlibApi
    fun performMqttSwitchOperation (macID: String, data : String) {
        val res = LANManager.getTcpData(data.removePrefix("{").removeSuffix("}"))
        if (res.size == 2 && res[0] == "CAAG") {
            Log.e("TronX","Received the bell ring")
            notificationReceived.value = "Bell alert\nSomeone is ringing the bell!"
        }
        else if (res.size >= 3 && "${res[0][0]}${res[0][1]}${res[0][2]}" == "CAA") {
            CacheHubData.switchDataUpdate(macID,  CacheHubData.getAreaID(macID, res[1].toInt()), res[1].toInt(), LANManager.getTcpSwitchData(res[2]))
        }
        /*
        else if (res.size == 3 && res[0] == "CAAD") {
            CacheHubData.switchDataUpdate(macID,  CacheHubData.getAreaID(macID, res[1].toInt()), res[1].toInt(), LANManager.getTcpSwitchData(res[2]))
        }*/
    }
}