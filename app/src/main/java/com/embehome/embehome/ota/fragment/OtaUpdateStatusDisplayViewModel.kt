package com.embehome.embehome.ota.fragment

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Services.LocalNetworkClient
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.ota.OtaDeviceDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtaUpdateStatusDisplayViewModel : ViewModel() {


    val downloadProgress = MutableLiveData <Int> (0)
    val installProgress = MutableLiveData <Int> (0)
    val downloadScreen = MutableLiveData(false)
    val installScreen = MutableLiveData (false)
    val doneInstallationScreen = MutableLiveData (false)

    val downloadVersionMessage = MutableLiveData("")
    val updateDetails = MutableLiveData<OtaDeviceDetails> ()

    fun setDetails (type : String, version : String, link : String, size : String) {
        updateDetails.value = OtaDeviceDetails(type, version, true, link, size)
        downloadVersionMessage.value = "There is update available for $type, version $version."
        AppServices.log("Getting data for update $type, $version,  $link, $size")
    }

    fun back(context : Context, navController: NavController) {
        if (downloadScreen.value == true)
            AppServices.toastShort(context, "Please wait while update in progress")
        else navController.navigateUp()
    }

    @ExperimentalStdlibApi
    fun startDownloading (context: Context, activity : Activity) {
        downloadScreen.value = true
        initiateUpdateWithHub(context, activity)
    }

    @ExperimentalStdlibApi
    fun initiateUpdateWithHub (context : Context, activity : Activity) = viewModelScope.launch {
        if (updateDetails.value != null) {
            val ver = updateDetails.value?.latest_version ?: ""
            val res = LocalNetworkClient.otaTcpHandler(
                CacheHubData.selectedHubIP, LANManager.getHubOtaCmd(
                    ver,
                    updateDetails.value?.file_size ?: "",
                    updateDetails.value?.download_path ?: ""
                ), Integer.parseInt(updateDetails.value?.file_size ?: "0"), downloadProgress
            )
            AppServices.log("TronX_OTA", "Socket closed - ${res.status} ${res.data}")
            if (res.status) {
                downloadProgress.value = 100
//                AppServices.toastShort(context, "Socket closed - ${res.status} ${res.data}")
                val cloudResult = HttpManager.updateHubVersion(CacheHubData.selectedMacID, ver)
                AppServices.log("Hub version update in cloud is ${cloudResult.status}")
                CacheHubData.getHub(CacheHubData.selectedMacID)?.let {
                    it.version = ver
                }
                installScreen.value = true
                for (i in 0..10000 step 100) {
                    installProgress.value = i * 100 / 10000
                    delay(100)
                }
                doneInstallationScreen.value = true
            }
//            else AppServices.toastShort(context, "Socket closed - ${res.status} ${res.data}")
            else {
                AppDialogs.startMsgLoadScreen(context, "${res.data}. Please wait..")
                delay(10000)
            }
            AppDialogs.stopLoadScreen()
        }
    }

    fun closeUpdateScreen (activity: Activity) {
        activity.finish()
    }

}