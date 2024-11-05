package com.embehome.embehome.irb.irb_repository

import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

object IRBOperationRepository {


    suspend fun addRemote (
            macID : String,
            thing_serial_number : String,
            thing_id : Int,
            remote_name : String,
            remote_id : Int,
            ir_category : String,
            ir_model : String,
            ir_data : HashMap<String,String>
        ) = withContext(Main) {
        val remoteData  = RemoteCloudModel (
                macID,
                thing_serial_number,
                thing_id,
                remote_name,
                remote_id,
                ir_category,
                ir_model,
                ir_data
        )
        HttpManager.addRemote(remoteData)
    }

    fun deleteRemote () {

    }

    @ExperimentalStdlibApi
    suspend fun readIRValue (ip: String, thingsID: Int) = withContext(IO) {
        val res = LANManager.readIRValue(CacheHubData.selectedHubIP, thingsID)
        if (res.status) {
            return@withContext res.data.removeRange(0,7)

        }
        else "Failed"
    }

    @ExperimentalStdlibApi
    /*suspend fun playButton (ip : String, thingsID :Int, ir : String) = withContext(IO){
        if (ip.length > 5) {
            LANManager.sendIRData(
                CacheHubData.selectedHubIP,
                thingsID,
                ir
            )
        }
        else {
            MqttClient.publishOnMqtt(CacheHubData.selectedMacID, LANManager.irbPlayCmd(thingsID, 38000, ir))
        }
    }*/

    fun playButtonSequentially () {

    }


}