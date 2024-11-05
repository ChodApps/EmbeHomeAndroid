package com.embehome.embehome.irb

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.irb.model.CloudIrbRemoteList
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext


/** com.tronx.tt_things_app.irb
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 02-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object CacheRemoteData {

    private val irbList = MutableLiveData<ArrayList<BoardDetails>>(ArrayList())
    private val remoteList = MutableLiveData<HashMap<String,ArrayList<RemoteCloudModel>>>(HashMap())


    fun generateRemoteID (serialNo: String) : Int {
        val ids = CacheSceneTwoWay.getIDs(99)
        val remote = getRemoteList(serialNo)
        remote.forEach {
            if (ids.contains(it.remote_id)) {
                ids[ids.indexOf(it.remote_id)] = 999
            }
        }
        return ids.min() ?: 1
    }

    fun addCreateRemote (serialNo: String, remote : RemoteCloudModel) {
        val list = getRemoteList(serialNo)
        list.add(remote)
    }

    fun deleteRemote (serialNo: String, remoteId : Int) {
        val list = getRemoteList(serialNo)
        list.filter { it.remote_id == remoteId }.apply {
            list.removeAll(this)
        }
    }

    fun getRemote (serialNo: String, remoteID : Int): RemoteCloudModel? {
        val r = getRemoteList(serialNo)
        r.forEach {
            if (it.remote_id == remoteID) {
                return it
            }
        }
        return null
    }

     fun getRemoteList (serialNo: String): ArrayList<RemoteCloudModel>  {
        return ((if (remoteList.value != null){
            if (remoteList.value!![serialNo] == null) {
                remoteList.value!![serialNo] = ArrayList()
            }
            remoteList.value!![serialNo]
        } else {
            remoteList.value = HashMap()
            remoteList.value!![serialNo] = ArrayList()
            remoteList.value!![serialNo]
        })!!)
    }

    suspend fun getIRBRemoteList (macID : String, serialNo : String) = withContext(Main) {
        if (remoteList.value == null) {
            remoteList.value = HashMap()
        }
        if (remoteList.value!![serialNo].isNullOrEmpty()) {
            getAllRemoteForIRB(macID, serialNo)
//            remoteList.value!![serialNo] = ArrayList()
        }
        remoteList.value!![serialNo] ?: ArrayList()
    }

    suspend fun getAllRemoteForIRB (macID : String, serialNo : String) = withContext(Main) {
        val res = HttpManager.getRemote(macID, serialNo)
        if (res.status) {
            try {
                val data = (res.body as CloudIrbRemoteList).data
                val remoteList = getRemoteList(serialNo)
                data.forEach { remote ->
                    if (remoteList.size == 0) {
                        remoteList.add(remote)
                    }
                    else {
                        remoteList.filter { it.remote_id == remote.remote_id }.also {
                            if (it.isEmpty()) {
                                remoteList.add(remote)
                            }
                        }
                    }
                }
            }
            catch (e : Exception) {
                Log.d("TronX _ Remote", e.message.toString())
            }
        }
    }

    fun addIRBToCache (irb : BoardDetails) {
        if (irb.category == "IRB") {
            irbList.value?.forEach {
                if (it.thing_id != irb.thing_id) {
                    irbList.value!!.add(irb)
                }
            }
        }
    }

    suspend fun getIRBForScene (macID: String) = withContext(Main){
        val irb = CacheHubData.getAllIRBList(macID)
        val result = ArrayList<BoardDetails>()
        irb.forEach {board ->
            val remotes = getIRBRemoteList(macID, board.thing_serial_number)
            if (remotes.size >= 1) {
                remotes.filter { it.ir_data.size > 0 }.also {
                    if (it.size > 0)
                        result.add(board)
                }
            }
        }
        result
    }

    suspend fun getRemoteForScene (macID: String, serialNo: String) = withContext(Main){
        getIRBRemoteList(macID, serialNo).filter {
            it.ir_data.size > 0
        }
    }

    fun getIrbDetail (thingsID : Int): BoardDetails? {
        val irb = irbList.value?.filter { it.thing_id == thingsID }
        return if (irb != null) irb[0] else null
    }

    fun getIrbList (): ArrayList<BoardDetails>? {
        return if (irbList.value?.size!! > 0) irbList.value else null
    }

    fun removeIRBFromCache (irb : BoardDetails) {
        if (irb.category == "IRB") {
            irbList.value?.forEach {
                if (it.thing_id == irb.thing_id) {
                    irbList.value!!.remove(irb)
                }
            }
        }
    }

}