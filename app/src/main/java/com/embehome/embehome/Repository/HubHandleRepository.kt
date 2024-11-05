package com.embehome.embehome.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Manager.SSBManager
import com.embehome.embehome.Model.*
import com.embehome.embehome.Services.LocalNetworkClient
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.NetworkHosts
import com.embehome.embehome.getDaoFromDevice
import com.embehome.embehome.room.dao.RDHubDetails
import com.embehome.embehome.room.dao.RDSSBDetails
import com.embehome.embehome.room.db.dao.TronXDB
import com.embehome.embehome.room.entity.REHubDetails
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext


/** com.tronx.tt_things_app.Repository
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 20-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object HubHandleRepository {


    @ExperimentalStdlibApi
    suspend fun addHub(context: Context, hubData : HubUdpData, hubName : String, image : String, wifiSSID : String) = withContext(Main){
        AppDialogs.startLoadScreen(context)
        val result = HttpManager.hubONBoard(hubData, hubName , image, wifiSSID)
        if (result.status) {
            val tcpResult = getHubUpdateDataTcp(hubData.ip, (result.body as CloudHubDataReceiver).data["hub_auth_token"].toString(), result.body.data["hub_refresh_token"].toString())
            if (tcpResult.status) {
                val data = LANManager.getTcpData(tcpResult.data)
                if (data.size == 4) {
                    val updateResult = HttpManager.updateHubDetails(hubData.macID, data[2], data[3])
                    if (updateResult.status) {
                        /*val db = getDB(context)
                        if (db!=null) {*/
//                            insert(db.hubDao(), hubData.macID, hubName, hubData.version.toString())
                            val h = HubData (hubData.macID, hubName, hubData.version.toString(), MutableLiveData(hubData.ip), image, wifiSSID)
                            CacheHubData.addHub(h)
                            Toast.makeText(context, "Hub Saved Successfully",Toast.LENGTH_SHORT).show()
//                        }
                        true.also {
//                            AppDialogs.stopLoadScreen()
                        }
                    }
                    else {
                        //Delete Board From Hub and Delete Board From Cloud
                            val error = updateResult.body as HttpErrorModel
                        if (error.error_code == 500002L) {
                            deleteHubFromCloud(hubData.macID)
                        }
                        AppServices.toastShort(context, error.message)
                        false.also {
                            AppDialogs.stopLoadScreen()
                        }
                    }
                }
                else {
                    Log.d("TronX","tcp failed while getting PanID")
                    Toast.makeText(context, "Failed to connect to hub",Toast.LENGTH_SHORT).show()
                    false.also {
                        AppDialogs.stopLoadScreen()
                    }
                }
            }
            else {
                Log.d("TronX",tcpResult.data)
                Toast.makeText(context, "Hub " + tcpResult.data,Toast.LENGTH_SHORT).show()
                deleteHubFromCloud(hubData.macID)
                false.also {
                    AppDialogs.stopLoadScreen()
                }
            }

        }
        else {
            Log.d("TronX","Filed to send Data to cloud or ${result.body as String}")
            Toast.makeText(context, "Something went wrong." as String,Toast.LENGTH_SHORT).show()
//            deleteHubFromCloud(hubData.macID)
            false.also {
                AppDialogs.stopLoadScreen()
            }
        }
    }

    private suspend fun deleteHubFromCloud(macID: String) {
        HttpManager.deleteHub(macID)
    }

    suspend fun updateHubData (context: Context, macID: String, hubName: String, image: String, version: String, wifiSSID: String) = withContext(Main) {
        AppDialogs.startLoadScreen(context)
        val res = HttpManager.updateHubData(macID, hubName, image, version, wifiSSID)
        res.status.also {
            CacheHubData.updateHub(macID, hubName, image, version, wifiSSID)
            AppDialogs.stopLoadScreen()
        }
    }

    @ExperimentalStdlibApi
    suspend fun getHubUpdateDataTcp (ip : String, hubToken : String, refreshToken : String) = withContext(IO){
        //TODO Ip Check Mandatory
        LocalNetworkClient.writeOnTcp(
            ip,
            NetworkHosts.TCP_PORT,
            LANManager.getHubRegistrationCmd(hubToken, refreshToken)
        )
    }

    fun deregisterHubLan(ip : String) {

    }

    fun updateHubData() {

    }

    @ExperimentalStdlibApi
    suspend fun addDevice (context: Context, areaID : Int, boardName : String, macID: String) = withContext(Main) {
        val ssbID = CacheHubData.generateHubID(macID)
        val result = LANManager.addDevice(ssbID.toString())
        if (result.status) {
            Log.d("TronX", "Hub Response For Device ADD ${result.data}")
            val res = SSBManager.prepareBoardAddData(
                result.data,
                areaID,
                boardName,
                ssbID
            )
            if (res.thing_serial_number != "") {
                Log.d("TronX","Data to send ${res.thing_serial_number} ${res.thing_id} ${macID}")
                val httpResult = HttpManager.addSwitchBoard( macID, res)
                if (httpResult.status) {
                    display(context,"Device added to Cloud successfully")
                    CacheHubData.setAreaWithData(macID, res.area_id, res)
                    /*val db = getDB(context)
                    if (db != null) {
                        insertDeviceDB(db.ssbDao(), res, macID)
                    }*/
                    RequestFeedback(true, res)
                } else {
                    display(context,
                        httpResult.body as String
                    )
                    display(context,"Reset board and try again")
                    RequestFeedback(false, "Reset board and try again")
                }
            }
            else  {
                display(context, "Device Not Found")
                RequestFeedback(false, "")
            }
        } else {
            display(context, result.data)
            RequestFeedback(false, "")
        }
    }

    private fun display(context: Context, msg : String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun deleteSSB() {

    }

    fun updateSSB() {

    }

    suspend fun getDB (context: Context) = withContext(IO) {
        TronXDB.getDB(context.applicationContext)
    }

    suspend fun insert (dao : RDHubDetails, macID: String, name : String, version : String) = withContext(IO) {
        dao.insertHubData(REHubDetails(macID, name, version))
    }

    suspend fun insertDeviceDB (dao : RDSSBDetails, device : BoardDetails, macID: String) = withContext(IO) {
        val data = getDaoFromDevice(device, macID)
        dao.insertBoardData(data)
    }


}