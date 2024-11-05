package com.embehome.embehome.Repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Model.*
import com.embehome.embehome.Services.MqttClient
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.scene.IRSceneModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object HubFeatureHandleRepository {

    ///**************************SCENE OPERATIONS******************************///

    @ExperimentalStdlibApi
    suspend fun addScene (macID: String, ip: String, sceneNameID : Int, switchData : ArrayList<SceneSwitchDetailModel> = ArrayList(), irData : ArrayList<IRSceneModel> = ArrayList(), subScene : ArrayList<Int> = ArrayList()) = withContext(Main){
        val id = CacheSceneTwoWay.generateSceneID(macID)
        val sceneData = SceneCloudModel (macID, id, sceneNameID, device_list = switchData, ir_data = irData, subscene_list = subScene)
        val lanRes = LANManager.createScene(ip, id, switchData, irData) /*LanRequestFeedback(true, "")*/
        if (lanRes.status) {
            val httpResult = HttpManager.createScene(sceneData)
            if (httpResult.status) {
                Log.d("TronX", "Scene Created Successfully")
                CacheSceneTwoWay.addSceneTOCache(macID, sceneData)
            } else {
                Log.d("TronX", httpResult.body as String)
            }
            return@withContext httpResult.status
        }
        lanRes.status
    }

    @ExperimentalStdlibApi
    suspend fun updateScene (macID: String, ip: String, sceneNameID : Int, sceneOldId : Int, data : ArrayList<SceneSwitchDetailModel>) = withContext(Main) {
        val sceneNewId = CacheSceneTwoWay.generateSceneID(macID)
        val scene = SceneCloudUpdateModel (macID, sceneOldId, sceneNewId, sceneNameID, data, ArrayList())
        val lanRes = LANManager.updateScene(ip, sceneOldId, sceneNewId, data, ArrayList())
        if (lanRes.status) {
            val httpRes = HttpManager.updateScene(scene)
            if (httpRes.status) {
                Log.d("TronX", "Scene Updated Successfully")
            } else {
                Log.d("TronX", httpRes.body as String)
            }
            CacheSceneTwoWay.addSceneTOCache(macID, scene)
            return@withContext httpRes.status
        }
        lanRes.status
    }

    @ExperimentalStdlibApi
    suspend fun updateSceneRemote (macID: String, ip: String, sceneNameID : Int, sceneOldId : Int, updateRemote : Boolean, deviceData : ArrayList<SceneSwitchDetailModel>, data : ArrayList<IRSceneModel>, subScene : ArrayList<Int> = ArrayList()) = withContext(Main) {
        val sceneNewId = CacheSceneTwoWay.generateSceneID(macID)
        val scene = SceneCloudUpdateModel (macID, sceneOldId, sceneNewId, sceneNameID, deviceData, data)
        val lanRes = if (updateRemote) LANManager.updateScene(ip, sceneOldId, sceneNewId, ArrayList(), data) else LANManager.updateScene(ip, sceneOldId, sceneNewId, deviceData, ArrayList())
        if (lanRes.status) {
            val httpRes = HttpManager.updateScene(scene)
            if (httpRes.status) {
                Log.d("TronX", "Scene Updated Successfully")
                CacheSceneTwoWay.addSceneTOCache(macID, scene)
            } else {
                Log.d("TronX", httpRes.body as String)
            }
            return@withContext httpRes.status
        }
        lanRes.status
    }


    @ExperimentalStdlibApi
    fun playScene (context: Context, ip: String, macID : String, sceneId: Int, subScene: ArrayList<Int>)  {
        GlobalScope.launch (Main){
            if (ip.length > 1) {
                LANManager.playScene(ip, sceneId, subScene)
            }
            else {
                MqttClient.publishOnMqtt(context, macID, LANManager.playSceneCmd(sceneId, subScene))
            }
        }
    }

    @ExperimentalStdlibApi
    suspend fun deleteScene (context: Context, macID: String, ip :String, sceneId : Int) = withContext(Main){
        val res = LANManager.deleteScene(ip, sceneId)
        if (res.status) {
            val result = HttpManager.deleteScene(macID, sceneId)
            AppServices.log(result.status.toString())
            if (result.status) {
                CacheSceneTwoWay.deleteScene(macID, sceneId)
                AppServices.toastShort(context, "Scene Delete Successfully")
            }
            result.status
        }
        else {
            AppServices.toastShort(context, "Scene Failed to Delete from Hub")
            res.status
        }
    }

    fun updateScene () {

    }


    ///******************* TWO WAY OPERATIONS ***************************///

    @ExperimentalStdlibApi
    suspend fun addTwoWay (activity: Activity, macID : String, ip : String, data : TwoWaySwitchDetails) : Boolean = withContext(Main){
        if (ip.length > 5) {
            val id = CacheSceneTwoWay.generateTwoWayId(macID)
            val lanResult = LANManager.createTwoWay(ip, id, data)
            AppServices.log(lanResult.data)
            if (lanResult.status) {
                val twoWayData = TwoWayItemModel(macID, id, "Two Way", data)
                val result = HttpManager.createTwoWay(twoWayData)
                if (result.status) {
                    CacheSceneTwoWay.addElementInTwoWayData(macID, twoWayData)
                    Log.d("TronX", "Two Way Created Successfully")
                    AppServices.toastShort(activity, "Two Way Created Successfully")
                } else {
                    Log.d("TronX", result.body as String)
                    AppServices.toastShort(activity, "Fail to create two way")
                }
                return@withContext result.status
            }
            return@withContext lanResult.status
        }
        AppServices.toastShort(activity, "Hub not available")
        false
    }

    @ExperimentalStdlibApi
    suspend fun deleteTwoWay (context : Context, ip : String, macID: String, twoWayData :TwoWayItemModel) = withContext(Main) {
            val lanResult = LANManager.deleteTwoWay(ip, twoWayData)
            if (lanResult.status){
                val result = HttpManager.deleteTwoWay(macID, twoWayData.twoway_id)
                AppServices.log(result.status.toString())
                if (result.status) {
                    CacheSceneTwoWay.deleteElementInTwoWayData(macID, twoWayData.twoway_id)
                    AppServices.toastShort(context, "Two way deleted successfully")
                    true
                }
                else{
                    AppDialogs.openMessageDialog(context, "Failed to delete two way.")
                    false
                }
            }
            else {
                AppDialogs.openMessageDialog(context, "Failed to delete two way.")
                false
            }
    }


}