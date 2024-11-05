package com.embehome.embehome.viewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Services.LocalNetworkClient
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.irb.CacheRemoteData
import com.embehome.embehome.irb.model.RemoteCloudModel
import kotlinx.coroutines.launch

class RoomRemoteViewModel : ViewModel () {

    val actionTrigger = MutableLiveData(true)
    var action = ""

//    val hubIp = CacheHubData.getIp(CacheHubData.selectedMacID)


    val irbRemoteData : MutableLiveData<HashMap <String,ArrayList<RemoteCloudModel>>> = MutableLiveData(HashMap())
    val data  = MutableLiveData<List<BoardDetails>>()
    val irbList = CacheHubData.getAllIRBList(CacheHubData.selectedMacID).also {
        viewModelScope.launch {
            it.forEach {ssb ->
                CacheRemoteData.getIRBRemoteList(CacheHubData.selectedMacID, ssb.thing_serial_number).also {
                    /*if (it.size != 0) {*/
                        addIrbData(ssb.thing_serial_number, it)
//                    }
                }
            }
        }
    }

    fun addIrbData (sno : String, data : ArrayList<RemoteCloudModel>) {
        irbRemoteData.value!![sno] = data.also {
            performAction("refreshAdapter")
        }
    }

    var irb : BoardDetails? = null

    fun addRemote () {
        performAction("AddRemote")
    }


    val read = MutableLiveData(false)
    val reading = MutableLiveData(View.GONE)
    val play = MutableLiveData (false)
    val playing = MutableLiveData (View.GONE)

    val findIrbStatus = MutableLiveData("IRB Not Found")
    val readIrbStatus = MutableLiveData("Read Status")
    val playIrbStatus = MutableLiveData("Play Status")
    val irbData = MutableLiveData("Implementation Is Pending")

    fun findIRB () {
        data.value = CacheHubData.getAreaIRB(CacheHubData.selectedMacID, CacheHubData.selectedAreaId).also {
            Log.d ("TronX",it?.toList().toString())
        }
        if (data.value != null && data.value!!.isNotEmpty()) {
            irb = data.value!![0]
            if (irb != null && irb!!.thing_id > 0) {
                read.value = true
                findIrbStatus.value = "IRB Found!!"
            }
        }
    }

    fun home () {
        performAction("Exit")
    }

    @ExperimentalStdlibApi
    fun read () = viewModelScope.launch{
        reading.value = View.VISIBLE
        read.value = false
        readIrbStatus.value = "Reading IR value"
        val ip = CacheHubData.selectedHubIP
        if (ip.length > 5) {
            val cmd = LANManager.irbReadCmd(irb?.thing_id!!, 38000).also {
                Log.d("TronX",it)
            }
            val res = LocalNetworkClient.readIRValue(ip, cmd)
            if (res.status) {
                readIrbStatus.value = "SUCCESS"
                irbData.value = res.data.substring(13)
                Log.d("TronX","irb data - ${res.data}")
                play.value = true
            }
            else
                readIrbStatus.value = res.data
        }
        else
        readIrbStatus.value = ip
        reading.value = View.GONE
        read.value = true
    }

    @ExperimentalStdlibApi
    fun play () = viewModelScope.launch {
        read.value = false
        play.value = false
        playing.value = View.VISIBLE
        playIrbStatus.value = "Playing IR value"
        val ip = CacheHubData.selectedHubIP
        if (ip.length > 5) {
            val cmd = LANManager.irbPlayCmd(irb?.thing_id!!,38000, irbData.value!!).also {
                Log.d ("TronX","irb play cmd - $it")
            }
            LocalNetworkClient.writeOnTCPWithOutResult(ip, cmd)
            playIrbStatus.value = "SUCCESS"
        }
        else
        playIrbStatus.value = ip
        playing.value = View.GONE
        play.value = true
        read.value = true
    }

    fun performAction (operation : String) {
        action = operation
        actionTrigger.value = true
        actionTrigger.value = false
    }
}