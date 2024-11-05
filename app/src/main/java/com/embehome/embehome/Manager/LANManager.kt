package com.embehome.embehome.Manager

import android.util.Log
import com.embehome.embehome.Model.*
import com.embehome.embehome.Services.LocalNetworkClient
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.Utils.NetworkHosts
import com.embehome.embehome.rules.RuleCondition
import com.embehome.embehome.rules.model.*
import com.embehome.embehome.scene.IRSceneModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.util.*
import kotlin.collections.ArrayList


/** com.tronx.tt_things_app.Manager
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 19-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


@ExperimentalStdlibApi
object LANManager {

    private const val irbFrequencyDefault : Int = 38000
    var isAddDevice = false
    var isTCPInUse = false

    ///************ Hub Related CMD ****************///
    fun getHubDiscoveryCmd (udpData : String = "") = "{UDPA^${AppServices.getUDPDiscoveryData()}}".toByteArray()
    fun getHubRegistrationCmd (appToken : String, refreshToken : String) = "{AAAA^$appToken^$refreshToken}"
    fun getHubUnregisterCmd () = "{ABAA}"
    fun getHubOtaCmd (version : String, size : String, link : String) = "{ACAA^$version^$size^$link}"
    fun getHubIpMqttCmd () = "{AACC}"
    fun getHubIpTcpCmd () = "{AACA}"

    ///*********** SSB Related CMD ****************///
    fun getAddDeviceCmd (deviceID : String) = "{BAAA^$deviceID}"
    fun getDeleteDeviceCmd (deviceID : String) = "{BABA^$deviceID}"
    fun getSwitchCmd (deviceID : Int, switchID : Int, deviceStatus : Int) = "{BCAA^$deviceID*$switchID!$deviceStatus}"
    fun getSleepCmd (deviceID: Int, action : Int) = "{BCBA^$deviceID!$action}"
    fun getSetTouchConfigCmd (deviceID: Int, touchLevel : Int) = "{BACA^$deviceID^1^$touchLevel}"
    fun getTouchConfigCmd (deviceID: Int) ="{BADA^$deviceID}"

    ///************* Curtain Related CMD ***************///
    fun getConfigCurtain (deviceID: Int, status : Int) = "{BCEA^$deviceID*1!$status}"

    fun getReConfigCurtain (deviceID: Int, status : Int) = "{BCFA^$deviceID*1!$status}"

    fun getOperateCurtain (deviceID: Int, status : Int) = "{BCAA^$deviceID^1!$status}"

    ///************* Scene Related CMD **************///
    fun getSceneCmd (sceneID : Int, data : ArrayList<SceneSwitchDetailModel>): String {
        val cmd = "BECA^$sceneID"
        return "{$cmd${getSceneString(data)}}"
    }
    fun getEditSceneCmd (oldSceneID: Int, newSceneId : Int, data: ArrayList<SceneSwitchDetailModel>) = "{BEEA^$oldSceneID^$newSceneId${getSceneString(data)}}"

    fun playSceneCmd (sceneID: Int) = "{BEFA^$sceneID}"
    fun playSceneCmd (sceneId: Int, sceneID: ArrayList<Int>) : String {
        var ids = ""
        sceneID.forEach {
            ids += ",$it"
        }
        return "{BEFA^$sceneId$ids}"
    }

    fun getIRSceneCmd (sceneID: Int, irData: ArrayList<IRSceneModel>) : String {
        var ir = ""
        var irTime = ""
        irData.forEachIndexed{ i, it ->
            irTime = "$irTime,${if(i == irData.lastIndex) 0 else(it.time_interval * 1000).toInt()}"
            ir = "$ir*${it.data}"
        }
        return if (irData.size > 0) "{EBAA^$sceneID^${irData[0].thing_id}^${irData.size}^${irTime.drop(1)}^${ir.drop(1)}}" else "{EBAA^$sceneID}"
    }

    fun getEditIRSceneCmd (oldSceneID: Int, sceneID: Int, irData: ArrayList<IRSceneModel>) : String {
        var ir = ""
        var irTime = ""
        irData.forEachIndexed{ i, it ->
            irTime = "$irTime,${if(i == irData.lastIndex) 0 else(it.time_interval * 1000).toInt()}"
            ir = "$ir*${it.data}"
        }
        return if (irData.size > 0) "{EBBA^$oldSceneID^$sceneID^${irData[0].thing_id}^${irData.size}^${irTime.drop(1)}^${ir.drop(1)}}" else "{EBBA^$sceneID}"
    }

    fun getDeleteSceneCmd (sceneID: Int) = "{BEDA^$sceneID}"

    ///***************** Two Way Related CMD ****************///
    private fun getTwoWayCreateCmd(id: Int, data: TwoWaySwitchDetails) = "{BEAA^$id^${data.pri_thing_id}*${data.pri_switchId}^${data.sec_thing_id}*${data.sec_switchId}}"
    private fun getDeleteTwoWayCmd (id: Int, pid : Int, sid : Int) = "{BEBA^$id^$pid^$sid}"

    ///******************* IRB Related CMD ******************///
    fun irbReadCmd(deviceID: Int, frequency : Int) = "{EABA^$deviceID^$frequency}"
    fun irbPlayCmd(deviceID: Int, frequency: Int, irData : String) = "{EAAA^$deviceID^$irData}"

    fun getSceneString (data : ArrayList<SceneSwitchDetailModel>): String {
        val tempThingsId = ArrayList<Int>()
        var cmd = ""
        data.forEach {
            if (!tempThingsId.contains(it.thing_id)) {
                tempThingsId.add(it.thing_id)
            }
        }
        tempThingsId.forEach {thingsId ->
            cmd += "^$thingsId"
            data.forEach {
                if (thingsId == it.thing_id)
                    cmd += "*${it.switchId}!${it.switchstatus}"
            }
        }
        return cmd
    }


    ///******************* Rules Related CMD ******************///

    fun getRuleTimeCmd (ruleId : Int, time : RuleTimer, data : RuleDeviceDataDetail) : String {
        var days = ""

        time.weekdays.forEach {
            days += ",$it"
        }
        days = days.drop(1)

        var td = ""
        /*if (data.scenes_list.size > 0) {
            data.scenes_list.forEach {
                td += ",$it"
            }
            td = "^${td.drop(1)}"

        }
        else {
            td = getSceneString(data.device_list)
        }*/
        if (data.device_list.size > 0) {
            /*data.scenes_list.forEach {
                td += ",$it"
            }
            td = "^${td.drop(1)}"*/
            val t = data.device_list[0]
            td = "^0^${t.thing_id}*${t.switchId}!${t.switchstatus}"

        }
        else {
            td = "^1^"
            data.scenes_list.forEach {
                td += "$it,"
            }
            td = td.dropLast(1)
        }
        return  "{BBAA^$ruleId^${time.time}^${if (time.repeat) 1 else 0}^$days$td}"
    }

    fun getRuleSensorCmd (ruleId : Int, sensor : RuleSensor, data : RuleDeviceDataDetail) : String {
        var td = ""
        if (data.device_list.size > 0) {
            /*data.scenes_list.forEach {
                td += ",$it"
            }
            td = "^${td.drop(1)}"*/
            val t = data.device_list[0]
            td = "^0^${t.thing_id}*${t.switchId}!${t.switchstatus}"

        }
        else {
            td = "^1^"
            data.scenes_list.forEach {
                td += "$it,"
            }
            td = td.dropLast(1)
        }
        return "{BBCA^$ruleId^${sensor.thing_id}*${sensor.sensor_id}!${sensor.sensor_status}^${sensor.sensor_condition}$td}"
    }

    fun getRuleCounterCmd (ruleId : Int, time : Int, data : RuleDeviceDataDetail) : String {
        val t = data.device_list[0]
        return "{BBEA^$ruleId^${t.thing_id}*${t.switchId}!${t.switchstatus}^$time}"
    }

    fun getEditRuleTimeCmd (oldRuleId : Int, newRuleId: Int, time : RuleTimer, data : RuleDeviceDataDetail) : String{
        var days = ""

        time.weekdays.forEach {
            days += ",$it"
        }
        days = days.drop(1)

        var td = ""
        /*if (data.scenes_list.size > 0) {
            data.scenes_list.forEach {
                td += ",$it"
            }
            td = "^${td.drop(1)}"

        }
        else {
            td = getSceneString(data.device_list)
        }*/
        if (data.device_list.size > 0) {
            /*data.scenes_list.forEach {
                td += ",$it"
            }
            td = "^${td.drop(1)}"*/
            val t = data.device_list[0]
            td = "^0^${t.thing_id}*${t.switchId}!${t.switchstatus}"

        }
        else {
            td = "^1^"
            data.scenes_list.forEach {
                td += "$it,"
            }
            td = td.dropLast(1)
        }
        return "{BBBA^$oldRuleId^$newRuleId^${time.time}^${if (time.repeat) 1 else 0}^$days$td}"
    }

    fun getEditRuleSensorCmd (oldRuleId : Int, newRuleId: Int, sensor : RuleSensor, data : RuleDeviceDataDetail) : String{
        var td = ""
        /*if (data.scenes_list.size > 0) {
            data.scenes_list.forEach {
                td += ",$it"
            }
            td = "^${td.drop(1)}"

        }
        else {
            td = getSceneString(data.device_list)
        }*/
        if (data.device_list.size > 0) {
            /*data.scenes_list.forEach {
                td += ",$it"
            }
            td = "^${td.drop(1)}"*/
            val t = data.device_list[0]
            td = "^0^${t.thing_id}*${t.switchId}!${t.switchstatus}"

        }
        else {
            td = "^1^"
            data.scenes_list.forEach {
                td += "$it,"
            }
            td = td.dropLast(1)
        }
        return "{BBDA^$oldRuleId^$newRuleId^${sensor.thing_id}*${sensor.sensor_id}!${sensor.sensor_status}^${sensor.sensor_condition}$td}"
    }

    fun getEditRuleCounterCmd (oldRuleId : Int, newRuleId : Int, time : Int, data : RuleDeviceDataDetail) : String{
        val t = data.device_list[0]
        return "{BBFA^$oldRuleId^$newRuleId^${t.thing_id}*${t.switchId}!${t.switchstatus}^$time}"
    }

    fun getDeleteRule (ruleId : Int) = "{BBGA^$ruleId}"


/**************************************************************************************************************************/
    suspend fun getUdpData (data : ByteArray) =
        Arrays.copyOfRange(data, 1, data.indexOf(125)).decodeToString().split("^")

    fun getTcpData (data : String) = data.split("^")
    fun getTcpSwitchData (data : String) = data.split("!")

    suspend fun byteArrayToString(data : ByteArray) : LanRequestFeedback {
         try {
            if (data.contains('}'.toByte())) {
                val res = data.decodeToString().substring(1, 5)
                if ((data.size > 7 && data[6] == '0'.toByte()) || res == "AACB") {
                    return LanRequestFeedback(
                        true,
                        data.decodeToString(1, data.indexOf('}'.toByte()))
                    )
                } else if (data.size > 7 && data[1] == 'C'.toByte()){
                    return LanRequestFeedback(
                        true,
                        data.decodeToString(1, data.indexOf('}'.toByte())))
                }
                else {
                    return LanRequestFeedback (false, "Request failed in HUB")
                }
            }

         } catch (e : Exception) {
                return LanRequestFeedback(false, "Found Data but failed to Decode ${e.message}")
         }

        return LanRequestFeedback(false, "Connection Failed")
    }

    suspend fun addDevice(deviceID: String) = withContext(Dispatchers.IO){
        //TODO validate deviceID
        if (isAddDevice)
            return@withContext LanRequestFeedback(false,"Previous request is in Process")

        isAddDevice = true
        LocalNetworkClient.writeOnTcpTemp(CacheHubData.selectedHubIP, NetworkHosts.TCP_PORT, getAddDeviceCmd(deviceID)).also { Log.d("TronX","Add board data received ${it.data}")
            isAddDevice = false
        }
    }

    var isPerformAction = false

    /*suspend fun getIP(macID : String) = withContext(Dispatchers.IO) {
        if (FakeDB.currentHubData.macID == macID)
            return@withContext FakeDB.currentHubData
        FakeDB.hubData.forEach{
             if (it.macID == macID && it.ip.length > 7)
                 return@withContext it
            }
        FakeDB.hubData = getAllHubInLan()
        if (FakeDB.hubData.size == 0)
            return@withContext HubUdpData("",0,0,"","")

        FakeDB.hubData.forEach {
            if (it.macID == macID) {
                FakeDB.currentHubData = it
                return@withContext it
            }
        }
        HubUdpData("",0,0,"","")
    }*/

    suspend fun getAllHubInLan() = withContext(Dispatchers.IO){
        LocalNetworkClient.getHub(LANManager.getHubDiscoveryCmd())
    }

    suspend fun performAction(deviceID : Int, switchID: Int, deviceStatus: Int) : List<String>? {
        if (isPerformAction)
            return null

        isPerformAction = true
        Log.d("TronX","Sending data to ${FakeDB.currentHubData.ip}")
        val result = LocalNetworkClient.writeOnTcp(FakeDB.currentHubData.ip, NetworkHosts.TCP_PORT, getSwitchCmd(deviceID, switchID, deviceStatus )).also {
            Log.d("TronX","Tcp Action performed ${it.data}")
            isPerformAction = false
        }
        isPerformAction = false
        val data = getTcpData(result.data)
        if (data.size == 2) {
            if (data[0] == "BCBA") {
                return getTcpSwitchData(data[1])
            }
        }
        return null
    }

    suspend fun performSwitchAction(deviceID : Int, switchID: Int, deviceStatus: Int, ip : String) : LanRequestFeedback = withContext(Dispatchers.IO){
        if (isPerformAction)
            return@withContext LanRequestFeedback(false, "Previous Request is in Process")

        isPerformAction = true.also {
            GlobalScope.launch(Dispatchers.IO) {
                delay(LocalNetworkClient.operationTcpDelay.toLong())
                isPerformAction = false
            }
        }
        LocalNetworkClient.writeOnTcpToPerformOperation(ip, getSwitchCmd(deviceID, switchID, deviceStatus )).also { isPerformAction = false }
    }

    suspend fun performSleepAction(deviceID : Int,  deviceStatus: Int, ip : String) : LanRequestFeedback = withContext(Dispatchers.IO) {
        if (isTCPInUse)
            return@withContext LanRequestFeedback(false, "Previous Request is in Process")

        isTCPInUse = true
        val cmd = getSleepCmd(deviceID,  deviceStatus )
        Log.d("TronX","Sending data $cmd to $ip ")
        LocalNetworkClient.writeOnTcpToPerformOperation(ip, cmd ).also { isTCPInUse = false }
    }

    suspend fun deleteBoard(deviceID: Int) = withContext(Dispatchers.IO){
        LocalNetworkClient.writeOnTcp(CacheHubData.selectedHubIP, NetworkHosts.TCP_PORT, getDeleteDeviceCmd(deviceID.toString() ), 20)
    }

    suspend fun deleteHub() = withContext(Dispatchers.IO) {
        LocalNetworkClient.writeOnTcp(CacheHubData.selectedHubIP, NetworkHosts.TCP_PORT, getHubUnregisterCmd(), 20)
    }

    suspend fun configCurtain(deviceID : Int, status: Int) = withContext(Dispatchers.IO) {
        LocalNetworkClient.writeOnTcp(CacheHubData.selectedHubIP, NetworkHosts.TCP_PORT, getConfigCurtain(deviceID, status))
    }

    suspend fun reConfigCurtain(deviceID : Int, status: Int) = withContext(Dispatchers.IO) {
        LocalNetworkClient.writeOnTcp(CacheHubData.selectedHubIP, NetworkHosts.TCP_PORT, getReConfigCurtain(deviceID, status))
    }

    suspend fun operateCurtain(deviceID : Int, status: Int) = withContext(Dispatchers.IO) {
        LocalNetworkClient.writeOnTcp(CacheHubData.selectedHubIP, NetworkHosts.TCP_PORT, getOperateCurtain(deviceID, status))
    }

    suspend fun createScene (ip : String, sceneID: Int, sceneData : ArrayList<SceneSwitchDetailModel>, irData : ArrayList<IRSceneModel>) = withContext(IO) {
        val cmd = if (sceneData.size > 0 ) getSceneCmd(sceneID, sceneData) else getIRSceneCmd(sceneID, irData)

        AppServices.log(cmd)
        LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd)
    }

    suspend fun updateScene (ip : String, oldSceneID: Int, newSceneId: Int, data : ArrayList<SceneSwitchDetailModel>, irData: ArrayList<IRSceneModel>) = withContext(IO) {
        val cmd = if (data.size > 0) getEditSceneCmd(oldSceneID, newSceneId, data) else getEditIRSceneCmd(oldSceneID, newSceneId, irData)
        AppServices.log("TronX _ IRScene Edit",cmd)
        LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd)
    }



    suspend fun playScene (ip: String, sceneID: Int, subScene : ArrayList<Int>) = withContext(IO){
        val cmd = playSceneCmd(sceneID, subScene)
        val res = LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd)
        AppServices.log(cmd)
        AppServices.log(res.data)
    }

    suspend fun deleteScene (ip: String, sceneID: Int) = withContext(IO) {
        val cmd = getDeleteSceneCmd(sceneID)
        LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd)
    }

    suspend fun createTwoWay (ip : String, id : Int, data : TwoWaySwitchDetails) = withContext(IO) {
        val cmd = getTwoWayCreateCmd (id, data).also {
            Log.d("TronX", it)
        }
        val res = LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd)
        AppServices.log(res.data)
        LanRequestFeedback(res.data[5] == '0', res.data)
    }

    suspend fun deleteTwoWay (ip : String, data : TwoWayItemModel) = withContext(IO) {
        val cmd = getDeleteTwoWayCmd(data.twoway_id, data.device_details.pri_thing_id, data.device_details.sec_thing_id)
        val res = LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd)
        AppServices.log(res.data)
        res
    }


    ///***************************IR Functions*********************///

    suspend fun readIRValue (ip : String, deviceID: Int) = withContext(IO){
        val cmd = irbReadCmd(deviceID, irbFrequencyDefault)
        LocalNetworkClient.readIRValue(ip, cmd)
    }

    suspend fun sendIRData (ip : String, deviceID: Int, irData: String) = withContext(IO) {
        val cmd = irbPlayCmd(deviceID, irbFrequencyDefault, irData)
        LocalNetworkClient.writeOnTCPWithOutResult(ip,  cmd)
    }


    ////************************** Rules Function ***********************///////

    suspend fun createRule (ip :String, rule : RuleDetails
    ) = withContext(IO) {
        var cmd = ""
        when (RuleCondition.valueOf(rule.rule_type)) {
           RuleCondition.COUNTER -> {
               rule.rule_data.rule_details.counter?.let {
                   cmd = getRuleCounterCmd(rule.rule_id, it.stop_counter, rule.rule_data.rule_action)
               }
           }
            RuleCondition.EVENT -> {
                rule.rule_data.rule_details.sensor?.let {
                    cmd = getRuleSensorCmd(rule.rule_id, it, rule.rule_data.rule_action)
                }
            }

            RuleCondition.TIMER -> {
                rule.rule_data.rule_details.timer?.let {
                    cmd = getRuleTimeCmd(rule.rule_id, it, rule.rule_data.rule_action)
                }
            }
        }
        Log.d("TronX _ Rule", cmd)
        LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd, 20)
    }

    suspend fun editRule (ip :String, rule : RuleUpdateDetails) = withContext(IO) {
        var cmd = ""
        when (RuleCondition.valueOf(rule.rule_type)) {
            RuleCondition.COUNTER -> {
                rule.rule_data.rule_details.counter?.let {
                    cmd = getEditRuleCounterCmd(rule.rule_id_old, rule.rule_id_new, it.stop_counter, rule.rule_data.rule_action)
                }
            }
            RuleCondition.EVENT -> {
                rule.rule_data.rule_details.sensor?.let {
                    cmd = getEditRuleSensorCmd(rule.rule_id_old, rule.rule_id_new, it, rule.rule_data.rule_action)
                }
            }

            RuleCondition.TIMER -> {
                rule.rule_data.rule_details.timer?.let {
                    cmd = getEditRuleTimeCmd(rule.rule_id_old, rule.rule_id_new, it, rule.rule_data.rule_action)
                }
            }
        }
        Log.d("TronX _ Rule", cmd)
        LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd, 20)
    }

    suspend fun deleteRule (ip : String, ruleId : Int) = withContext(IO) {
        LocalNetworkClient.writeOnTcp(ip,  NetworkHosts.TCP_PORT, getDeleteRule(ruleId), 20)
    }

    suspend fun getTouchSensitivityLevel (ip: String, deviceID: Int) = withContext(IO) {
        val cmd = getTouchConfigCmd(deviceID)
        LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd)
    }

    suspend fun setTouchSensitivityLevel (ip: String, deviceID: Int, touchLevel: Int) = withContext(IO) {
        val cmd = getSetTouchConfigCmd(deviceID, touchLevel)
        LocalNetworkClient.writeOnTcp(ip, NetworkHosts.TCP_PORT, cmd)
    }

}