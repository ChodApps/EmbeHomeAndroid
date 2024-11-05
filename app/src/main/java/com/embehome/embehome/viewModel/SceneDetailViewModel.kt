package com.embehome.embehome.viewModel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SceneDetailViewModel : ViewModel() {

    val image = "IMAGE"
    val option = "OPTION"
    val sub = "SUB_SCENE"

    val device = "Device"
    val remote = "IR"
    val create = "Create"

    val displayOnHomeScreen = MutableLiveData(false)
    val performAction = MutableLiveData(false)
    var action = ""
    val sceneName = MutableLiveData("")
    var sceneItemId = -1

    val subScene = MutableLiveData<ArrayList<Int>>(ArrayList())

    val submitButtonText = MutableLiveData ("Add Switches")
    val submitRemoteText = MutableLiveData ("Add Remote")

    val selectImage = MutableLiveData(View.GONE)
    val chooseOption = MutableLiveData (View.GONE)
    val chooseSubScene = MutableLiveData (View.GONE)
    val deviceView = MutableLiveData (View.VISIBLE)
    val remoteView = MutableLiveData (View.VISIBLE)

    fun updateView (v : String) {
        when(v) {
           image -> {
               selectImage.value = View.VISIBLE
               chooseOption.value = View.GONE
               chooseSubScene.value = View.GONE
           }

            option -> {
                selectImage.value = View.GONE
                chooseOption.value = View.VISIBLE
                chooseSubScene.value = View.GONE
            }

            sub -> {
                selectImage.value = View.GONE
                chooseOption.value = View.GONE
                chooseSubScene.value = View.VISIBLE
            }

            device -> {
                remoteView.value = View.GONE
            }

            remote -> {
                deviceView.value = View.GONE
            }

            create -> {
                remoteView.value = View.VISIBLE
                deviceView.value = View.VISIBLE
            }

            else -> {
                selectImage.value = View.GONE
                chooseOption.value = View.GONE
                chooseSubScene.value = View.GONE
            }
        }
    }

    fun selectName () {
        updateView(image)
    }

    fun createScene () {
        performAction("CreateScene")
    }

    fun addIrbButtons () {
        performAction("IRB")
    }

    fun addScene () {
        performAction("Scene")
    }

    fun addSwitches () {
        performAction("Add")
    }

    fun openSceneItemSelectDialog () {
        performAction("SelectSceneItem")
    }

    fun sceneBack() {
        performAction("Back")
    }

    fun performAction(data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

}