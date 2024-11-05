package com.embehome.embehome.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Activity.PowerTimeDiff
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.PowerUnitModel
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.coroutines.launch

class ChartsDisplayViewModel : ViewModel() {

    val data = MutableLiveData<PowerUnitModel>()

    val time = MutableLiveData<PowerTimeDiff>(PowerTimeDiff.DAY)

    fun getSwitchPowerNumber (sno :String, id : Int) = viewModelScope.launch {
     val d =   HttpManager.getSwitchPowerData(sno, id)
        if (d.status) {
            try {
                data.value = d.body as PowerUnitModel
                val p = HttpManager.getTodaySwitchPowerData(sno, id)
                if (p.status) {

                }
            }
            catch (e : Exception) {
                AppServices.log("TronX _ Power", e.message.toString())
            }
        }

    }

    fun getDevicePowerNumber (sno :String) = viewModelScope.launch {
        val d =   HttpManager.getDevicePowerData(sno)
        if (d.status) {
            try {
                data.value = d.body as PowerUnitModel
                val p = HttpManager.getTodayDevicePowerData(sno)
                if (p.status) {

                }
            }
            catch (e : Exception) {
                AppServices.log("TronX _ Power", e.message.toString())
            }
        }
    }

    fun getAreaPowerNumber (id : Int) = viewModelScope.launch {
        val d =   HttpManager.getAreaPowerData(CacheHubData.selectedMacID,  id)
        if (d.status) {
            try {
                data.value = d.body as PowerUnitModel
                val p = HttpManager.getTodayAreaPowerData(CacheHubData.selectedMacID, id)
                if (p.status) {

                }
            }
            catch (e : Exception) {
                AppServices.log("TronX _ Power", e.message.toString())
            }
        }
    }


}