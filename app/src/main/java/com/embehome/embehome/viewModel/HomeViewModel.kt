package com.embehome.embehome.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.CloudHubDataList
import com.embehome.embehome.Model.HubCloudData
import com.embehome.embehome.Model.HubData
import com.embehome.embehome.Repository.DataRequestRepository
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 18-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/



class HomeViewModel : ViewModel() {
    init {

    }

    var addHub = MutableLiveData<Boolean>()
    val profile = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val showToast = MutableLiveData<Boolean>()
    val toastMessage = MutableLiveData<String>()
    val hubData = MutableLiveData<ArrayList<HubCloudData>>()
    val hubDataUpdated = MutableLiveData<Boolean>()
    @ExperimentalStdlibApi
    val implHubData = CacheHubData.getMacIdList()

    val tempHubList = MutableLiveData<ArrayList<HubData>>(ArrayList())

    init {
        addHub.value = false
        hubDataUpdated.value = false
       // fetchHubData()
    }


    fun addHub() {
        addHub.value = true
        addHub.value = false
    }

    fun fetchHubData() {
        viewModelScope.launch {
            val result = HttpManager.fetchCloudData()
            if (result.status) {
                try {
                    val temp = (result.body as CloudHubDataList).data
                    if (!(hubData.value != null && hubData.value == temp)) {
                        hubData.value = temp
                        hubDataUpdated.value = true
                        hubDataUpdated.value = false
                    }
                }
                catch (e : Exception) {
                    displayToast("Failed to process Cloud Data ") //TODO Delete before release
                    Log.d("TronX","Failed to Parse fetch data ${e.message}")
                }
            }
        }
    }

    fun getHubData (context : Context, hubList : MutableLiveData<ArrayList<HubData>>) = viewModelScope.launch {
        DataRequestRepository.getHubList(context, hubList)
    }

    fun userProfile() {
        profile.value = true
        profile.value = false
    }

    @ExperimentalStdlibApi
    fun refreshHubData() {
        displayToast("Please wait... Refresh in progress")
        //fetchHubData()
        CacheHubData.getMacIdList()
    }

    fun openIPCamera() {
        displayToast("Feature coming soon.")
    }

    fun openSmartBulb() {
        displayToast("Feature coming soon.")
    }

    fun openNotification () {
        displayToast("NOTIFICATION")
    }

    private fun displayToast(msg : String) {
        toastMessage.value = msg
        showToast.value = true
        showToast.value = false
    }
}