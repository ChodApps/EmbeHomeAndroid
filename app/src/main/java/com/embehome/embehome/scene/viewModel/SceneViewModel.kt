package com.embehome.embehome.scene.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Model.SceneCloudModel
import com.embehome.embehome.Repository.HubFeatureHandleRepository
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.coroutines.launch

class SceneViewModel : ViewModel() {

    var action = ""
    val performAction = MutableLiveData (false)
    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    val sceneName = MutableLiveData ("Scene Name")
    var scene = MutableLiveData<SceneCloudModel>()
    val newSceneId = MutableLiveData<Int>()



    fun getScene (sceneId : Int) {
        scene.value = CacheSceneTwoWay.getScene(CacheHubData.selectedMacID, sceneId)
    }

    fun updateScene (sceneId: Int) {
        val t = CacheSceneTwoWay.getScene(CacheHubData.selectedMacID, sceneId)
        if (t != null) {
            scene.value = t
        }
    }

    fun delete () {
        performAction("DeleteScene")
    }

    @ExperimentalStdlibApi
    fun delete (context: Context) = viewModelScope.launch {
        scene.value?.scene_id?.let {
            AppDialogs.startLoadScreen(context)
            if (HubFeatureHandleRepository.deleteScene(context, CacheHubData.selectedMacID, CacheHubData.selectedHubIP,
                    it
                ))
                back()
            AppDialogs.stopLoadScreen()
        }

    }

    @ExperimentalStdlibApi
    fun deleteScene (context : Context) {
        AppDialogs.openTitleDialog(context, "Delete Scene","Do you want to Delete Scene - ${sceneName.value} ?", "No", "Yes"){d, w ->
            delete(context)
        }
    }

    fun editScene () {
        performAction("Edit")
    }

    fun back () {
        performAction("Back")
    }
}