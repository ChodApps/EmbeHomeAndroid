package com.embehome.embehome.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.embehome.embehome.Fragments.RoomMenuDirections
import com.embehome.embehome.Manager.HUBManager
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.ota.OtaCloudResponse
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 16-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RoomMenuViewModel : ViewModel() {

    val showToast = MutableLiveData<Boolean>()
    val toastString = MutableLiveData<String>()
    val openAddBoard = MutableLiveData<Boolean>()
    val hubVersion = MutableLiveData(CacheHubData.getHub(CacheHubData.selectedMacID)?.version)
    val hubName  = MutableLiveData (CacheHubData.getHub(CacheHubData.selectedMacID)?.name)
    val performAction = MutableLiveData(false)
    var action = ""

//    val hubIp = CacheHubData.getIp(CacheHubData.selectedMacID)


    fun displayToast(msg : String) {
        toastString.value = msg
        showToast.value = true
        showToast.value = false
    }

    fun performAction (Data : String) {
        action = Data
        performAction.value = true
        performAction.value = false
    }

    fun openTwoWay() {
        performAction("TwoWay")
    }

    fun hubReConfig () {
        performAction("HubReConfig")
    }

    fun updateHubDetails () {
        performAction("UpdateHub")
    }

    fun delete () {
        performAction("DeleteHub")
    }

    fun updateHubFirmware () {
        performAction("UpdateHubFirmware")
    }

    @ExperimentalStdlibApi
    fun delete(context: Context) {
        viewModelScope.launch {
//            displayToast("Deleting Hub ${FakeDB.macID}")
//
//            val result = HttpManager.deleteHub(FakeDB.macID)
//            if (result.status) {
//                displayToast("Hub Deleted Successfully")
//
//            }
//            else {
//                displayToast(result.body as String)
//            }

            AppDialogs.startLoadScreen(context)
            val res = HUBManager.deleteHub(CacheHubData.selectedMacID)
            if (res.status) {
                displayToast("Hub delete Successful")
                CacheHubData.deleteHub(CacheHubData.selectedMacID, CacheHubData.selectedAreaId)
                performAction("Finish")
            }
            else
                displayToast("Hub delete Failed")
            AppDialogs.stopLoadScreen()
        }
    }

    @ExperimentalStdlibApi
    fun deleteHub (context : Context) {
        AppDialogs.openTitleDialog(context, "Delete Hub", "Do you want to Delete hub ?", "No","Yes") {d, r ->
            delete(context)
        }
    }

    fun activityLog () {
        performAction("Log")
    }

    fun home () {
        performAction("Finish")
    }

    fun addBoard() {
        openAddBoard.value = true
        openAddBoard.value = false
    }

    fun checkAndUpdateHubFirmware (context : Context, macId : String, navController : NavController) = viewModelScope.launch {
        AppDialogs.startMsgLoadScreen(context, "Checking for update. Please wait..")
        val result = HttpManager.checkDeviceOta(macId)
        if (result.status) {
            try {
                val response = result.body as OtaCloudResponse
                AppDialogs.stopLoadScreen()
                if (response.data.update_available) {
                    /*AppDialogs.openTitleDialog(context, "Update Available", "Update available for this hub, do you want to download the update.", "No", "Yes") {d, i ->
                        AppServices.toastShort(context, "Redirect to hub update screen")
                    }*/

                        val d = RoomMenuDirections.actionRoomMenuToOtaUpdateStatusDisplayFragment(type = response.data.module_type,version = response.data.latest_version, link = response.data.download_path ?: "", size = response.data.file_size ?: "0")
                        navController.navigate(d)
                }
                else AppServices.toastShort(context, "No update available for this hub")
            } catch (e : Exception) {

                AppDialogs.stopLoadScreen()

            }
        }
        else {
            AppDialogs.stopLoadScreen()
            AppServices.toastShort(context, result.body as String ?: "")
        }
    }
}