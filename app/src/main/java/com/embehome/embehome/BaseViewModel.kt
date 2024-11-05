package com.embehome.embehome

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Services.LocalNetworkClient
import com.embehome.embehome.Services.MqttClient
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.NetworkHosts
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel () {


    @ExperimentalStdlibApi
    fun getHubIp (context: Context, macId : String) = viewModelScope.launch {
        CacheHubData.getHub(macId)?.let {  hub ->
            val bssid = AppServices.wifiBSSID(context)
            if (!bssid.isNullOrEmpty() && hub.bssid == bssid) {
                AppServices.log("TronX_offline", "BSSID in matching and requesting for ip over tcp")
                val ip = AppServices.getToken(context, "ip$macId")
                if (ip.isNotEmpty() && ip.length > 5) {
                    getIpOverTCP(ip, context, macId)
                }
                else getIpOverMqtt(context, macId)
            }
        }
    }

    @ExperimentalStdlibApi
    private suspend fun getIpOverTCP (ip : String, context: Context, macId: String) {
        val res = LocalNetworkClient.writeOnTcpTemp(ip, NetworkHosts.TCP_PORT, LANManager.getHubIpTcpCmd())
        AppServices.log("TronX_offline", "Got response over tcp ${res.status}  --- ${res.data}")
        if (res.status) {
            val data = LANManager.getTcpData(res.data)
            AppServices.log("TronX_offline", "Got response over tcp ${data.toList()}")
            CacheHubData.getHub(macId)?.ip?.value = if (data.size >= 5 && data[3] == macId) data[4] else "".also { getIpOverMqtt(context, macId) }
        }
        else {
            getIpOverMqtt(context, macId)
        }
    }

    @ExperimentalStdlibApi
    private suspend fun getIpOverMqtt (context: Context, macId: String) {
        MqttClient.publishOnMqtt(context, macId, LANManager.getHubIpMqttCmd(), "AACB")
    }


}