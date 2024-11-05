package com.embehome.embehome.Utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.util.Log

import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Model.AreaData
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.HubData
import com.embehome.embehome.Repository.DataRequestRepository
import com.embehome.embehome.rules.utils.RuleDataRepository
import com.embehome.embehome.rules.utils.RuleOperationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.Utils
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 16-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object CacheHubData {

    const val homeNetwork = "Operation can be performed only in Home Network"


    var selectedAreaId = 0
    var selectedMacID = ""
    var selectedHubIP = ""

    private val AppHubData = MutableLiveData<HashMap<String,MutableLiveData<HashMap<Int,MutableLiveData<ArrayList<BoardDetails>>>>>>()
    private val macIDList = MutableLiveData<ArrayList<HubData>?>(ArrayList())
    private val areaDetails = MutableLiveData<ArrayList<AreaData>>()

    init {

    }

    fun removeArea (macID: String, id: Int) {
        try {
            AppHubData.value?.let { d ->
                d[macID]?.value?.let { h ->
                    h.remove(id)
                }
            }
        }
        catch (e : Exception) {
            Log.e("TronX","DeleteArea ${e.message}")
        }
    }

    fun addHub (h : HubData) {
        macIDList.value?.add(h)
    }


    fun deleteAll () {
        macIDList.value?.clear()
        AppHubData.value?.clear()
        CacheSceneTwoWay.twoWayData.clear()
        CacheSceneTwoWay.sceneData.clear()
        RuleDataRepository.deleteAll()
    }

    fun getAllDevices (macID: String) : ArrayList <BoardDetails> {
        val temp = ArrayList<BoardDetails>()
        AppHubData.value?.get(macID)?.let { area ->
            area.value?.keys?.forEach {
                area.value?.get(it)?.let {b ->
                    b.value?.let {l ->
                        temp.addAll(l)
                    }
                }
            }
        }
        return temp
    }

    @ExperimentalStdlibApi
    fun getAllIpConnectedInNetwork() {
        GlobalScope.launch(Main) {
            val lanResult = LANManager.getAllHubInLan()
            lanResult?.let {
                macIDList.value?.let {
                    it.forEach {c ->
                        if (lanResult.size > 0) {
                            lanResult.filter { lan ->
                                lan.macID == c.macID
                            }.also { res ->
                                if (res.isNotEmpty()) {
                                    c.ip.value = res[0].ip
                                } else {
                                    c.ip.value = ""
                                }
                            }
                        } else {
                            c.ip.value = ""
                        }
                    }
                }

                /*if (lanResult.size != 0) {
                    clearIP()
                    lanResult.forEach {
                        setIp(it.macID, it.ip)
                    }
                }*/
            }
        }
    }

    fun deleteHub (macID: String, areaID: Int) {
        macIDList.value?.filter { it.macID == macID }.also {
            it?.let { it1 -> macIDList.value?.removeAll(it1) }
        }
        AppHubData.value?.contains(macID).also {
            if (it == true)
                AppHubData.value?.remove(macID)
        }
    }

    fun updateHub (macID: String, hubName: String, image: String, version: String, wifiSSID: String) {
        val hub = getHub(macID)
        hub?.let {
            val h = HubData (macID, hubName, version,it.ip, image, wifiSSID)
            macIDList.value?.remove(hub).also {
                if (it == true) {
                    macIDList.value?.add(h)
                }
            }
        }
    }

    fun deleteHubData ( macID: String) {
        if (AppHubData.value != null && AppHubData.value?.containsKey(macID)!!) {
            AppHubData.value!!.remove(macID)
        }
    }

    fun getAllIRBList (macID: String): List<BoardDetails> {
        val list = ArrayList<BoardDetails>()
        val data = getHubMacIDData(macID)
        if (data.value != null) {
            if (data.value!!.isNotEmpty()){
                data.value!!.keys.forEach {
                    if (data.value!![it]?.value != null) {
                        data.value!![it]?.value?.filter { b -> b.thing_type == "IRB" || b.thing_type == "ISB" }?.forEach {
                            list.add(it)
                        }
                    }
                }
            }
        }
        return list
    }

    fun getAllIRBListAddRemote (macID: String): List<BoardDetails> {
        val list = ArrayList<BoardDetails>()
        val data = getHubMacIDData(macID)
        if (data.value != null) {
            if (data.value!!.isNotEmpty()){
                data.value!!.keys.forEach {
                    if (data.value!![it]?.value != null) {
                        data.value!![it]?.value?.filter { b -> b.thing_type == "IRB" }?.forEach {
                            list.add(it)
                        }
                    }
                }
            }
        }
        return list
    }

    fun updateDeviceDetails (macID: String, thingsID: Int, name : String, areaID: Int) {
        try {
            val area = getHubAreaIDData(macID, areaID)
            val board = CacheSceneTwoWay.getBoardData(macID, thingsID)
            board?.let {
                if (it.area_id == areaID) {
                    it.thing_name = name
                } else {
                    val tb = BoardDetails (
                        it.thing_serial_number,
                        it.thing_type,
                        it.category,
                        areaID,
                        name,
                        it.thing_image,
                        it.thing_id,
                        it.switchesList,
                        it.thing_version
                    )
                    /*it.thing_name = name
                    val pAreaID = it.area_id
                    it.area_id = areaID*/
                    deleteBoardData(macID, it.area_id, it.thing_id)
                    setAreaWithData(macID, areaID, tb)
                }

            }
        }
        catch (e : Exception) {
            AppServices.log("TronX _ Device Update", e.message.toString())
        }

    }



    fun getAreaIRB (macID: String, areaID : Int): List<BoardDetails> {
        val boardDetails = getBoardDetails(macID, areaID)
        return boardDetails.value?.filter {
            it.thing_type == "IRB"
        } ?: ArrayList()
    }

    fun getIp(macID: String): MutableLiveData<String> {
        macIDList.value?.forEach {
            if (it.macID == macID) {
                selectedHubIP = it.ip.value ?: ""
                return it.ip
            }
        }
        return MutableLiveData<String>("")
    }

    fun getHub (macID: String) : HubData?{
        macIDList.value?.forEach {
            if (it.macID == macID) {
                return it
            }
        }
        return null
    }

    private fun setIp (macID: String, ip : String) {
        macIDList.value?.forEach {
            if (it.macID == macID)
                it.ip.value = ip
        }
    }

    private fun clearIP () {
        macIDList.value?.forEach {
            it.ip.value = ""
        }
    }

    fun addArea (id : Int, name : String, img : String, image : Bitmap?) {
        val obj = AreaData(id, name, img, false, image)
        areaDetails.value?.add(obj)
    }

    fun getArea(areaID: Int): AreaData {
        areaDetails.value?.forEach {
            if (it.area_id == areaID)
                return it
        }
        return AreaData(0,"","",true)
    }

    private suspend fun getAreaData() {
        if (areaDetails.value == null || areaDetails.value!!.size == 0)
            areaDetails.value = DataRequestRepository.getAreaData()
    }

    fun getAreaList () : ArrayList<AreaData>?{
        return areaDetails.value
    }

    @ExperimentalStdlibApi
    fun getMacIdList(): MutableLiveData<ArrayList<HubData>?> {
            GlobalScope.launch (Dispatchers.Main) {
                var tempData = DataRequestRepository.getHubIdList()
//                getAreaData()
                tempData?.let {
                    /*if (tempData.size > 0 && macIDList.value != null && macIDList.value!!.size == 0) {
                        macIDList.value = tempData
                    }
                    else if (tempData != macIDList.value) {
                        macIDList.value = tempData
                    }*/
                    if (macIDList.value == null) macIDList.value = ArrayList()
                    macIDList.value?.let {
                        if (it.size == 0) {
                            macIDList.value = tempData
                        }
                        else {
                            it.forEach {
                                tempData.filter { t-> t.macID == it.macID }.also {res ->
                                    if (res.isEmpty())
                                        macIDList.value?.remove(it)
                                    else {
                                     it.name = res[0].name
                                        it.version = res[0].version
                                        it.image = res[0].image
                                        it.bssid = res[0].bssid
                                        tempData.removeAll(res)
                                    }
                                }
                            }
                            if (tempData.size > 0) {
                                it.addAll(tempData)
                            }
                        }
                    }
                    macIDList.value = macIDList.value

                    tempData.forEach {
                        CacheSceneTwoWay.reqSceneList(it.macID)
//                    CacheSceneTwoWay.reqTwoWayData(it.macID)
                    }
                    getAllIpConnectedInNetwork()
                }
            }
        return macIDList
    }

    fun getHubData(macID: String): MutableLiveData<HashMap<Int, MutableLiveData<ArrayList<BoardDetails>>>> {
        getHubMacIDData(macID)
        GlobalScope.launch (Dispatchers.Main) {
            getAreaData()
            CacheSceneTwoWay.reqSceneItemData()
            val tempData : ArrayList<BoardDetails>? = DataRequestRepository.getHubData(macID)
            try {
                if (tempData != null) {
                    tempData.forEach {
                        it.switchesList.filter {
                            it.switchId == -1 || it.switchIconId.isEmpty()
                        }.let {r ->
                            it.switchesList.removeAll(r)
                        }
                        setAreaWithData(macID, it.area_id, it)
                    }
                    AppHubData.value?.get(macID)?.value?.forEach { m ->
                        m.value.value?.forEach { p ->
                            tempData.filter { c ->
                                c.thing_serial_number == p.thing_serial_number
                            }.also {
                                if (it.isEmpty()) {
                                    deleteBoardData(macID, m.key, p.thing_id)
                                }
                            }
                        }
                    }
                }
            }
            catch (e : Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            AppDialogs.stopLoadScreen()
            RuleOperationRepository.fetchRuleItemList()
            RuleOperationRepository.fetchRules(macID)
            CacheSceneTwoWay.reqTwoWayData(macID)
        }

        return AppHubData.value!![macID]!!
    }

    private fun getHubMacIDData(macID : String): MutableLiveData<HashMap<Int, MutableLiveData<ArrayList<BoardDetails>>>> {
        try {
            if (AppHubData.value == null)
                AppHubData.value = HashMap()
            if (!AppHubData.value!!.containsKey(macID))
                AppHubData.value!![macID] = MutableLiveData<HashMap<Int,MutableLiveData<ArrayList<BoardDetails>>>>()
        } catch (e: Exception) {
            AppServices.log(e.message.toString())
            AppHubData.value!![macID] = MutableLiveData<HashMap<Int,MutableLiveData<ArrayList<BoardDetails>>>>()
        }
        return AppHubData.value!![macID]!!
    }

    private fun getHubAreaIDData(macID: String, areaID: Int): MutableLiveData<ArrayList<BoardDetails>> {
        if (AppHubData.value!![macID]!!.value == null)
            AppHubData.value!![macID]!!.value = HashMap()
        if (!AppHubData.value!![macID]!!.value!!.containsKey(areaID))
            AppHubData.value!![macID]!!.value!![areaID] = MutableLiveData<ArrayList<BoardDetails>>()
        return AppHubData.value!![macID]!!.value!![areaID]!!
    }

    fun setAreaWithDataTemp(macID: String, areaID : Int, data : BoardDetails) {
        getHubMacIDData(macID)
        getHubAreaIDData(macID, areaID)
        if (AppHubData.value!![macID]!!.value!![areaID]!!.value == null)
            AppHubData.value!![macID]!!.value!![areaID]!!.value = ArrayList()
        if (AppHubData.value!![macID]!!.value!![areaID]!!.value!!.find { it.thing_id == data.thing_id } == null) {
            AppHubData.value!![macID]!!.value!![areaID]!!.value!!.add(data).also {
                if (it) {
                    if (FakeDB.deviceID <= data.thing_id) FakeDB.deviceID = data.thing_id + 1
                }
            }
        }
    }

    fun deleteDevices (macID: String) {
        val d = getHubMacIDData(macID)
        d.value?.clear()
    }

    fun setAreaWithData (macID: String, areaID : Int, data : BoardDetails) {
        getHubMacIDData(macID)
        getHubAreaIDData(macID, areaID)
        AppHubData.value?.get(macID)?.value?.get(areaID)?.let {
            if (it.value == null)
                AppHubData.value!![macID]!!.value!![areaID]!!.value = ArrayList()

            it?.value?.let {bl ->
                bl.filter { b ->
                    b.thing_id == data.thing_id
                }.also {rl ->
                    if (rl.isEmpty())
                        AppHubData.value!![macID]!!.value!![areaID]!!.value!!.add(data)
                    else if (rl.size == 1) {
                        val t = rl[0]
                        if (t.thing_id == data.thing_id && t.thing_serial_number == data.thing_serial_number) {
                            t.replicate(data)
                        }
                        else {
                            bl.remove(t)
                            bl.add(data)
                        }
                    }
                    else {
                        bl.size.let {
                            if (it == 0) {
                                AppHubData.value!![macID]!!.value?.remove(areaID)
                            }
                        }
                    }
                }
            }
        }
       /* if (AppHubData.value!![macID]!!.value!![areaID]!!.value == null)
            AppHubData.value!![macID]!!.value!![areaID]!!.value = ArrayList()
        if (AppHubData.value!![macID]!!.value!![areaID]!!.value!!.find { it.thing_id == data.thing_id } == null) {
            AppHubData.value!![macID]!!.value!![areaID]!!.value!!.add(data).also {
                if (it) {
                    if (FakeDB.deviceID <= data.thing_id) FakeDB.deviceID = data.thing_id + 1
                }
            }
        }*/
    }


    fun getBoardDetails (macID: String, areaID: Int): MutableLiveData<ArrayList<BoardDetails>> {
//        val hubData = getHubMacIDData(macID)
        return getHubAreaIDData(macID, areaID)
    }

    fun switchDataUpdate(macID: String, areaID: Int, thingsID: Int, switchData: List<String>) {
        val boardDetails = getBoardDetails(macID, areaID)
        boardDetails.value?.forEach {
            if (it.thing_id == thingsID) {
                it.switchesList.forEach { switchDetails ->
                    if (switchDetails.switchType != "E") {
                        val s = switchData[switchDetails.switchId - 1].toInt()
                        switchDetails.switchstatus = s
                        if (s != 0) {
                            switchDetails.switchLevel = s
                        }
                    }
                }.also {
                    if (boardDetails.value!!.size == 0) {
                        getHubMacIDData(macID).value?.remove(areaID)
                    }
                }
            }
        }
        val areaData = getHubAreaIDData(macID, areaID)
        areaData.value = areaData.value
    }

    fun deleteBoardData(macID: String, areaID: Int, thingsID: Int) {
        try {

            val boardDetails = getBoardDetails(macID, areaID)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                boardDetails.value?.removeIf {
                    it.thing_id == thingsID
                }
            }
            else {
                boardDetails.value?.filter {
                    it.thing_id == thingsID
                }?.also {
                    boardDetails.value?.removeAll(it)
                }
            }
            if (boardDetails.value?.size == 0)
                removeArea(macID, areaID)
        }
        catch (e : Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun deleteArea (macID: String, areaID: Int) {
        AppHubData.value?.get(macID)?.value?.remove(areaID)
    }

    @SuppressLint("SuspiciousIndentation")
    fun getBoardDetail(macID: String, areaID: Int, thingsID: Int) : BoardDetails? {
        val boardDetails = getBoardDetails(macID, areaID)
        boardDetails.value?.forEach {
            if(it.thing_id == thingsID)
            return it
        }
        return null
    }

    fun getBoardDetail(macID: String, areaID: Int, sno : String) : BoardDetails? {
        val boardDetails = getBoardDetails(macID, areaID)
        boardDetails.value?.forEach {
            if(it.thing_serial_number == sno)
                return it
        }
        return null
    }

    fun getAreaName (thingsId: Int): String {
        val areaID = getAreaID(selectedMacID, thingsId)
        val area = getArea(areaID)
        return area.area_name
    }

    @SuppressLint("SuspiciousIndentation")
    fun getAreaID(macID: String, thingsID: Int) : Int{
        val hubData = getHubMacIDData(macID)
        hubData.value?.forEach {
            it.value.value?.forEach { board ->
                if (board.thing_id == thingsID)
                return it.key
            }
        }
        return 0
    }

    fun getAreaID(macID: String, sno : String) : Int{
        val hubData = getHubMacIDData(macID)
        hubData.value?.forEach {
            it.value.value?.forEach { board ->
                if (board.thing_serial_number == sno)
                    return it.key
            }
        }
        return 0
    }

    fun generateHubID (macID: String): Int {
        val ids = CacheSceneTwoWay.getIDs(99)
        val data = getHubMacIDData(macID)
        try {
            if (data.value != null){
                data.value!!.forEach {
                    it.value.value?.forEach {
                        if (ids.contains(it.thing_id)) {
                            ids[ids.indexOf(it.thing_id)] = 9999
                        }
                    }
                }
            }
        }
        catch (e : Exception) {
            AppServices.log(e.message.toString())
        }
        return ids.min() ?: 1
    }
}