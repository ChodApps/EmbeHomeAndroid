package com.embehome.embehome.irb

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Services.MqttClient
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.irb.irb_repository.IRBOperationRepository
import com.embehome.embehome.irb.model.RemoteAddCloudModel
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


/** com.tronx.tt_things_app.irb
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 18-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object RemoteOperations {

    suspend fun saveRemote (context: Context, macID : String, sno : String, thingsID : Int, remoteName : String, remoteID : Int, category : String, irData : HashMap<String, String>) = withContext(Main) {
        AppDialogs.startMsgLoadScreen(context, "Saving Remote please Wait.")
        val remote = RemoteCloudModel (macID, sno, thingsID, remoteName, remoteID, category,"",irData)
        val res = HttpManager.addRemote(remote)
        if (res.status) {
            try {
                val r = res.body as RemoteAddCloudModel
                CacheRemoteData.addCreateRemote(sno, r.data)
            }
            catch (e : Exception) {
                Log.d("TronX", "${e.message}")
            }
            Toast.makeText(context, "Remote Added Successfully", Toast.LENGTH_SHORT).show()
        }else Toast.makeText(context, "Remote Add Failed ${res.body}", Toast.LENGTH_SHORT).show()
        res.status.also {
            AppDialogs.stopLoadScreen()
        }
    }

    @ExperimentalStdlibApi
    suspend fun operateRemote (context: Context,btnName : String, ir : String, operationType : String, thingsID: Int) = withContext(Main) {
        var irValue = ir
        var flag = 0
        if ((irValue.isNotEmpty() && !irValue.contains("null") && irValue.length > 5) && operationType != "Operate"){
            flag = 1000
            AppDialogs.openTitleDialog(context, "Read Remote Button Value",
                "You have already read value for this button do you want to read again or do you want to verify this button by Playing ?",
                "PLAY", "READ"
                ,{ d, m ->
                irValue = ""
            }, {d ->
                flag = 0
            })
        }

        while (flag != 0) delay(100)

        if ((irValue.isEmpty() || irValue.contains("null")) && operationType != "Operate") {
            AppDialogs.startMsgLoadScreen(context, "Reading value ...")
            delay(1000)
            val ird = IRBOperationRepository.readIRValue(CacheHubData.selectedHubIP, thingsID) /*"38000^3654,3583,476,1323,424,0^108,108,0^01232323242424242423232324242424242423242423242424232423232423232325,01232323242424242423232324242424242423242423242424232423232423232325,01232323242424242423232324242424242423242423242424232423232423232325"*/
            AppDialogs.stopLoadScreen()
            return@withContext ird
        }
        else if (!irValue.contains("null") && irValue.length > 10) {
//            AppServices.toastShort(context, "playing ${btn.name.substring(4)}")
            if (CacheHubData.selectedHubIP.length > 5) {
                LANManager.sendIRData(
                    CacheHubData.selectedHubIP,
                    thingsID,
                    irValue
                )
            }
            else {
                MqttClient.publishOnMqtt(context, CacheHubData.selectedMacID, LANManager.irbPlayCmd(thingsID, 38000, irValue.toString()))
            }
        }
        else
            AppServices.toastShort(context, "You did not perform IR read operation for this button at the time of creation")
        "nil"
    }

    suspend fun deleteRemote (context: Context, macID: String, sno: String, remoteID: Int, remoteName: String) = withContext(Main) {
        AppDialogs.startMsgLoadScreen(context, "Deleting Remote please Wait.")
        val res = HttpManager.deleteRemote(macID, sno, remoteID)
        if (res.status) {
            CacheRemoteData.deleteRemote(sno, remoteID)
            Toast.makeText(context, "Remote Deleted Successfully", Toast.LENGTH_SHORT).show()
        }else Toast.makeText(context, "Remote Delete Failed ${res.body}", Toast.LENGTH_SHORT).show()
        res.status.also {
            AppDialogs.stopLoadScreen()
        }
    }

    suspend fun updateRemote (context: Context, macID : String, sno : String, thingsID : Int, remoteName : String, remoteID : Int, category : String, irData : HashMap<String, String>) = withContext(Main) {
        AppDialogs.startMsgLoadScreen(context, "Updating Remote please Wait.")
        val remote = RemoteCloudModel (macID, sno, thingsID, remoteName, remoteID, category,"",irData)
        val res = HttpManager.updateRemote(remote)
        if (res.status) {
            CacheRemoteData.deleteRemote(sno, remote.remote_id)
            CacheRemoteData.addCreateRemote(sno, remote)
            Toast.makeText(context, "Remote Updated Successfully", Toast.LENGTH_SHORT).show()
        }else Toast.makeText(context, "Remote Update Failed ${res.body}", Toast.LENGTH_SHORT).show()
        res.status.also {
            AppDialogs.stopLoadScreen()
        }
    }
}
