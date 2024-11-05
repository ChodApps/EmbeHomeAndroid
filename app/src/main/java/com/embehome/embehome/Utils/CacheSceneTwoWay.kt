package com.embehome.embehome.Utils

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.embehome.embehome.Model.*
import com.embehome.embehome.Repository.DataRequestRepository
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import kotlin.random.Random

object CacheSceneTwoWay {

    val twoWayData = HashMap<String,MutableLiveData<ArrayList<TwoWayItemModel>>>()
    val sceneItemList = MutableLiveData<ArrayList<SceneItemModel>>()
    val sceneData = HashMap<String,MutableLiveData<ArrayList<SceneCloudModel>>>()

    var twoWayPid = -1
    var twopsno = ""
    var twossno = ""
    var twoWaySid = -1
    var twoWayPsid = -1
    var twoWaySsid = -1

    var sceneNameID = 1
    var sceneCreateSSBData = ArrayList<Int>()
    var sceneCreateSwitchData = ArrayList<SceneSwitchDetailModel>()

    fun clearTwoWay () {
        twoWayPid = -1
        twopsno = ""
        twossno = ""
        twoWaySid = -1
        twoWayPsid = -1
        twoWaySsid = -1
    }

    fun clearSceneCreateData () {
        sceneNameID = 1
        sceneCreateSwitchData.clear()
        sceneCreateSSBData.clear()
    }

    fun getScene (macID: String, sceneId: Int): SceneCloudModel? {
        val data = getSceneData(macID)
        data?.value?.forEach {
            if (it.scene_id == sceneId)
                return it
        }
        return null
    }

    fun addSceneItem (id : Int, name : String, img : String, image : Bitmap?) {
        val o = SceneItemModel(id, name, img, false, image)
        sceneItemList.value?.add(o)
    }

    fun getSceneItem (id : Int): SceneItemModel {
        sceneItemList.value?.forEach {
            if (it.scenename_id == id)
                return it
        }
        return sceneItemList.value?.get(0) ?: SceneItemModel(0, "Scene","",false)
    }

    fun addSceneTOCache (macID: String, scene : SceneCloudModel) {
        val d = getSceneData(macID)
        d?.value?.also {
            it.removeAll(it.filter { scene.scene_id == it.scene_id })
        }
        d?.value?.add(scene)
        d?.value = d?.value
    }

    fun addSceneTOCache (macID: String, scene : SceneCloudUpdateModel) {
        val d = getSceneData(macID)
        d?.value?.also {
            it.removeAll(it.filter { scene.scene_id_old == it.scene_id })
        }
        d?.value?.add(SceneCloudModel(scene.macID, scene.scene_id_new, scene.scenename_id, scene.device_list))
    }

    fun getSceneItemList () = sceneItemList.value

    fun deleteScene (macID: String, sceneId : Int) {
        val data = sceneData[macID]
        data?.value?.removeAll{
            it.scene_id == sceneId
        }
        data?.value = data?.value
    }


    suspend fun reqTwoWayData (macID : String) = withContext(Main){
        val res = DataRequestRepository.getTwoWayList(macID)
        if (res.size > 0) {
            deleteAllTwoWay(macID)
            updateTwoWayData(macID, res)
        }
    }

    fun updateTwoWayInSwitch (macID: String, tid : String?, dId : Int, sId : Int) {
        getBoardData(macID, dId)?.let {
            it.switchesList.forEach {
                if (it.switchId == sId)
                    it.twoway_id = tid
            }
        }
    }

    fun addElementInTwoWayData (macID: String, data : TwoWayItemModel) {
        if (twoWayData[macID] == null)
            twoWayData[macID] = MutableLiveData(ArrayList())
        twoWayData[macID]?.value?.removeAll{data.twoway_id == it.twoway_id}
        twoWayData[macID]?.value?.add(data)
        updateTwoWayInSwitch(macID, data.twoway_id.toString(), data.device_details.pri_thing_id, data.device_details.pri_switchId)
        updateTwoWayInSwitch(macID, data.twoway_id.toString(), data.device_details.sec_thing_id, data.device_details.sec_switchId)

    }

    fun deleteElementInTwoWayData (macID: String, data : Int) {
        if (twoWayData[macID] == null)
            twoWayData[macID] = MutableLiveData(ArrayList())

        twoWayData[macID]?.value?.let {
            it.filter {
                it.twoway_id == data
            }.let {
             it.forEach {data ->
                 updateTwoWayInSwitch(macID, null, data.device_details.pri_thing_id, data.device_details.pri_switchId)
                 updateTwoWayInSwitch(macID, null, data.device_details.sec_thing_id, data.device_details.sec_switchId)
             }
            }
        }
        twoWayData[macID]?.value?.removeAll{data == it.twoway_id}

    }

    suspend fun reqSceneItemData () = withContext(Main) {
        val res = DataRequestRepository.getSceneItemList()
        if (res.size > 0) {
            updateSceneItemData(res)
        }
    }

    suspend fun reqSceneList (macID: String) = withContext(Main) {
        val res = DataRequestRepository.getSceneList(macID)
        updateSceneData(macID, res)
    }

    fun getTwoWayData (macID: String) = if (twoWayData[macID] != null) twoWayData[macID] else MutableLiveData()

    fun getSceneData (macID: String): MutableLiveData<ArrayList<SceneCloudModel>>? {
        if (sceneData[macID] == null)
            sceneData[macID] = MutableLiveData(ArrayList())
        return sceneData[macID]
    }

    fun updateSceneData (macID: String, data : ArrayList<SceneCloudModel>) {
        if (sceneData[macID] == null)
        sceneData[macID] = MutableLiveData(data)
        else {
            sceneData[macID]?.value?.clear()
            sceneData[macID]?.value?.addAll(data)
        }
    }

    fun updateSceneItemData (data : ArrayList<SceneItemModel>) {
        sceneItemList.value = data
    }

    fun deleteAllTwoWay (macID: String) {
        twoWayData[macID]?.value?.clear()
    }

    fun updateTwoWayData (macID: String, res : ArrayList<TwoWayItemModel>) {
        twoWayData[macID] = MutableLiveData(res)
    }

    fun getBoardData (macID: String, thingsId : Int): BoardDetails? {
        val areaID = CacheHubData.getAreaID(macID, thingsId)
        return CacheHubData.getBoardDetail(macID, areaID, thingsId)
    }

    fun getBoardData (macID: String, sno : String) : BoardDetails? {
        val areaID = CacheHubData.getAreaID(macID, sno)
        return CacheHubData.getBoardDetail(macID, areaID, sno)
    }

    fun getBoardList (macID: String, thingsList : ArrayList<Int>): ArrayList<BoardDetails> {
        val data = ArrayList<BoardDetails>()
        thingsList.forEach {
            val result = getBoardData(macID, it)
            if (result != null && !data.contains(result)){
                data.add(result)
            }
        }
        return data
    }

    fun generateTwoWayId (macID: String): Int {
        val data = getTwoWayData(macID)
        var id = 1
        val ids = getIDs(99)
        if (data?.value != null && data.value!!.size > 0) {
            data.value!!.forEach {
                if (ids.contains(it.twoway_id)) {
                    ids[ids.indexOf(it.twoway_id)] = 999
                }
                /*if (it.twoway_id > id)
                    id = it.twoway_id + 1*/
            }
            return ids.min() ?: 1
        }
        return id
    }

    fun generateSceneID (macID: String) : Int {
        val ids = getIDs(99)
        val data = getSceneData(macID)
        return Random.nextInt(1, 60000)
        var id = 1
        if (data?.value != null && data.value!!.size > 0) {
            data.value!!.forEach {
                if (ids.contains(it.scene_id)) {
                    ids[ids.indexOf(it.scene_id)] = 999
                }
                /*if (it.twoway_id > id)
                    id = it.twoway_id + 1*/
            }
            return ids.min() ?: 1
        }
        return id

    }

    fun getIDs (limit : Int): Array<Int> {
        return Array(limit) { i -> i + 1}
    }



}