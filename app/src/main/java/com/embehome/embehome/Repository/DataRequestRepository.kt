package com.embehome.embehome.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.*
import com.embehome.embehome.room.dao.RDHubDetails
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext


/** com.tronx.tt_things_app.Repository
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 19-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object DataRequestRepository {

    suspend fun getHubList (context: Context, hubList: MutableLiveData<ArrayList<HubData>>) : Unit = withContext(Main) {
        /*val db = getDB(context)
        db?.hubDao()?.let {
            getHubDataFromDB(it).forEach {hub ->
                if (hub != null ) {
                    addHub(hubList, getHUBFromDao(hub))
                }
            }
        }*/
        val httpResult  = HttpManager.fetchCloudData()
        if (httpResult.status) {
            val data = httpResult.body as CloudHubDataList
            data.data.forEach {
                addHub(hubList, HubData(it.macID, it.hub_name, it.hub_version, MutableLiveData(""), it.hub_image, it.wifi_SSID))
            }
        }
    }

    fun addHub (hubList: MutableLiveData<ArrayList<HubData>>, hub : HubData) {
        hubList.value?.filter {
            it.macID == hub.macID
        }.also {
            if (it == null || it.isEmpty()) {
                hubList.value!!.add(hub)
                hubList.value = hubList.value
            }
        }

    }

    private suspend fun getHubDataFromDB (dao : RDHubDetails) = withContext(IO) {
        dao.getHubList()
    }

    private suspend fun getDB (context: Context) = withContext(IO) {
//        TronXDB.getDB(context)
    }

    suspend fun getHubIdList() = withContext(IO){
        val httpResult  = HttpManager.fetchCloudData()
        if (!httpResult.status) {
            Log.d("TronX", httpResult.body.toString())
            return@withContext null
        }
        val data = httpResult.body as CloudHubDataList
        val tempData = ArrayList<HubData>()
//        CacheSceneTwoWay.reqSceneItemData()
        data.data.forEach {
            tempData.add(HubData(it.macID, it.hub_name, it.hub_version, MutableLiveData(""), it.hub_image, it.wifi_SSID) )
            /*CacheSceneTwoWay.reqSceneList(it.macID)
            CacheSceneTwoWay.reqTwoWayData(it.macID)*/
        }
        tempData!!
    }

    suspend fun getHubData(macID : String)  = withContext(IO) {
        val httpResult = HttpManager.getAllBoards(macID)
        if (!httpResult.status) {
            Log.d("TronX", httpResult.body.toString())
            try {
                (httpResult.body as HttpErrorModel).error_code.let {
                    if (it == 400019L) {
                        ArrayList<BoardDetails>()
                    }
                }
            }
            catch (e : Exception){
                Log.e("TronX_Http","${e.message} failed to fetch error code")
            }
            return@withContext null
        }
//        CacheHubData.deleteDevices(macID)
        (httpResult.body as CloudBoardData).data
    }

    suspend fun getAreaData() = withContext(IO){
        val httpResult = HttpManager.getAreas()
        if (!httpResult.status) {
            Log.d("TronX", httpResult.body.toString())
            return@withContext ArrayList<AreaData>()
        }
        (httpResult.body as CloudAreaDataList).data
    }

    suspend fun getTwoWayList(macID: String) = withContext(Main) {
        val httpResult = HttpManager.getTwoWayList(macID)
        if (!httpResult.status) {
            Log.d("TronX", httpResult.body.toString())
            return@withContext ArrayList<TwoWayItemModel>()
        }
        (httpResult.body as CloudTwoWayList).data

    }

    suspend fun getSceneItemList () = withContext(Main) {
        val httpResult = HttpManager.getSceneItemList()
        if (!httpResult.status) {
            Log.d("TronX", httpResult.body.toString())
            return@withContext ArrayList<SceneItemModel>()
        }
        (httpResult.body as CloudSceneItemList).data

    }

    suspend fun getSceneList (macID: String) = withContext(Main) {
        val httpResult = HttpManager.getSceneList(macID)
        if (!httpResult.status) {
            Log.d("TronX", httpResult.body.toString())
            return@withContext ArrayList<SceneCloudModel>()
        }
        (httpResult.body as CloudSceneListRequest).data

    }


}
/*

class tempRepo (val dao : SSBDataDao) {
    val data : LiveData<List<String>> = dao.getSSBData()

    suspend fun insert (value : Int, data : String) {
        dao.insert(value, data)
    }

}*/
