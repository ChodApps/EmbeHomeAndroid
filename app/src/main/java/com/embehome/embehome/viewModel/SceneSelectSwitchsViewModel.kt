package com.embehome.embehome.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Model.SceneSwitchDetailModel
import com.embehome.embehome.Repository.HubFeatureHandleRepository
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.scene.IRSceneModel
import kotlinx.coroutines.launch

class SceneSelectSwitchsViewModel : ViewModel () {

    val performAction = MutableLiveData(false)
    var action = ""
    val expanded = MutableLiveData(-1)
    val selectedSwitch = MutableLiveData<ArrayList<SceneSwitchDetailModel>>(ArrayList())

    val submitButtonText = MutableLiveData("Save")

    fun save () {
        performAction("Save")
    }

    fun addRemote () {
        performAction("IRB")
    }

    fun back () {
        performAction("Back")
    }

    fun performAction (data : String) {
       action = data
        performAction.value = true
        performAction.value = false
    }

    @ExperimentalStdlibApi
    fun saveScene (context: Activity, macID: String,ip : String, sceneNameID: Int, data: ArrayList<SceneSwitchDetailModel>, subScene : ArrayList<Int>) = viewModelScope.launch {
        if (ip.length > 5) {
            try {
                AppDialogs.startLoadScreen(context)
                val result = HubFeatureHandleRepository.addScene(
                    macID,
                    ip,
                    sceneNameID,
                    switchData = data,
                    subScene = subScene
                )
                if (result) {
                    performAction("Scene Added Successfully")
                    CacheSceneTwoWay.clearSceneCreateData()
                    context.finish()
                } else {
                    performAction("Scene Failed to Add")
                }
                AppDialogs.stopLoadScreen()
            }
            catch (e : Exception) {
                AppServices.log("TronX _ Scene", "Scene add - ${e.message}")
                AppDialogs.stopLoadScreen()
            }
        }
    }

    @ExperimentalStdlibApi
    fun updateScene (context: Activity, macID: String, ip : String, sceneNameID: Int, sceneOldId : Int, data: ArrayList<SceneSwitchDetailModel>, irData : ArrayList<IRSceneModel>, subScene : ArrayList<Int>) = viewModelScope.launch {
        if (ip.length > 5) {
            try {
                AppDialogs.startMsgLoadScreen(context, "Saving Scene please wait")
                val result = HubFeatureHandleRepository.updateSceneRemote(
                    macID,
                    ip,
                    sceneNameID,
                    sceneOldId,
                    false,
                    data,
                    irData,
                    subScene
                )
                if (result) {
                    performAction("Scene Updated Successfully")
                    CacheSceneTwoWay.clearSceneCreateData()
                    context.finish()
                } else {
                    performAction("Scene Failed to Update")
                }
                AppDialogs.stopLoadScreen()
            }
            catch (e : Exception) {
                AppServices.log("TronX _ Scene", "Scene update - ${e.message}")
                AppDialogs.stopLoadScreen()
            }
        }
    }

}