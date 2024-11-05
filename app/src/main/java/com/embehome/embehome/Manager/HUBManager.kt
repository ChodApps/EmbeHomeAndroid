package com.embehome.embehome.Manager

import android.util.Log
import com.embehome.embehome.Model.CloudHubDataReceiver
import com.embehome.embehome.Model.HubUdpData
import com.embehome.embehome.Model.LanRequestFeedback
import com.embehome.embehome.Services.LocalNetworkClient
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.NetworkHosts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket


/** com.tronx.tt_things_app.Manager
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 05-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/



object HUBManager {

    @ExperimentalStdlibApi
    suspend fun hubDataParser(packet : DatagramPacket) : HubUdpData? {
        if (packet.data.contains(125)) {
            val splitData = LANManager.getUdpData(packet.data)
            if (splitData.size == 4 && splitData[0] == "UDPB")
                return HubUdpData(splitData[0], splitData[1].toInt(), splitData[2], splitData[3], packet.address.hostAddress)
        }
        return null
    }

    @ExperimentalStdlibApi
    suspend fun getHubToAdd()  = withContext(Dispatchers.IO){

        val udpPackets = LocalNetworkClient.getHub(LANManager.getHubDiscoveryCmd())
        val t = ArrayList<HubUdpData> ()
        try {
            udpPackets?.forEach {
                if (it.status == 0)
                    t.add(it)
            }

        }
        catch (e : Exception) {
            AppServices.log("TronX","Hub Parsing failed ${e.message}")
//            AppServices.log("TronX" , e.stackTrace.toList().toString())
        }
        udpPackets?.clear()
        t
    }

    @ExperimentalStdlibApi
    suspend fun addHub(hubData : HubUdpData, hubName : String, wifiSSID : String) = withContext(Dispatchers.IO) {
        Log.d("TronX", "Saving Hub Details on cloud")
        val regResult = HttpManager.hubONBoard(hubData, hubName,"", wifiSSID)
        Log.d("TronX","hubOnBoard result ${regResult.status} ${if(!regResult.status) "" else regResult.body!!.toString()}")
        if (regResult.status) {
            val res = LocalNetworkClient.writeOnTcp(
                hubData.ip,
                NetworkHosts.TCP_PORT,
                LANManager.getHubRegistrationCmd((regResult.body as CloudHubDataReceiver).data["hub_auth_token"].toString(), regResult.body.data["hub_refresh_token"].toString())
            )
            Log.d("TronX", "TCP packet received ${res.data} \n updating Hub Details")

            val data = if (res.status) LANManager.getTcpData(res.data) else ArrayList()
            val response = if (data.size == 4) HttpManager.updateHubDetails(hubData.macID, data[2], data[3]) else HttpManager.updateHubDetails(hubData.macID,"","").also {
                Log.d("TronX","Failed to Fetch Hub Serial Number and panID From TCP")
            }
            if (response.status) {
                return@withContext LanRequestFeedback(true,"Hub Added Successfully")
            }
        }
        LanRequestFeedback(false, "Request to Add Hub Failed")
    }

    private var isDeleteHub = false
    @ExperimentalStdlibApi
    suspend fun deleteHub(macID : String) = withContext(Dispatchers.IO) {
        if (isDeleteHub)
            return@withContext LanRequestFeedback(false,"Previous request is in Process")

        isDeleteHub = true
        val tcpResult = LANManager.deleteHub()
        if (!tcpResult.status){
            Log.d("TronX","Hub Failed to Unregister over TCP")
            return@withContext LanRequestFeedback(false,"Hub is not responding").also { isDeleteHub = false }
        }

        val httpResult = HttpManager.deleteHub(macID)
        if (!httpResult.status) {
            Log.d("TronX","Hub successfully unregistered but Failed to deleted from Cloud")
            return@withContext LanRequestFeedback(false,"Cloud is not responding ${httpResult.body as String}").also { isDeleteHub = false }
        }
        LanRequestFeedback(true,"Hub Deleted Successfully").also {
            CacheHubData.deleteHubData(macID)
            isDeleteHub = false }
    }

}