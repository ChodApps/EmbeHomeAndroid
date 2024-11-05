package com.embehome.embehome.Repository

import android.content.Context
import android.widget.Toast
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.room.dao.RDSSBDetails
import com.embehome.embehome.room.db.dao.TronXDB
import com.embehome.embehome.room.entity.RESSBDetails
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext


/** com.tronx.tt_things_app.Repository
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 11-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object DeviceHandleRepository {

    suspend fun getDeviceList (context: Context, macId : String, areaId : Int) : List<RESSBDetails> = withContext(Main) {
        val db = getDB(context)
        db?.ssbDao()?.let { getDeviceListFromDB(it, macId, areaId) } ?: ArrayList()
    }


    suspend fun getDB (context: Context) = withContext(IO) {
        TronXDB.getDB(context.applicationContext)
    }

    suspend fun getDeviceListFromDB (dao : RDSSBDetails, macId : String, areaId : Int) = withContext(IO) {
        dao.getAreaBoardData(macId, areaId)
    }

    suspend fun updateDeviceDetails (context: Context, macId: String, b : BoardDetails) = withContext(Main) {
        val res = HttpManager.updateDeviceDetails(macId, b)
        if (res.status) {
            Toast.makeText(context, "Updated Successful", Toast.LENGTH_SHORT).show()
            CacheHubData.updateDeviceDetails(macId, b.thing_id, b.thing_name, b.area_id)
        }
        else {
            Toast.makeText(context, res.body as String, Toast.LENGTH_SHORT).show()
        }
        res.status
    }

}