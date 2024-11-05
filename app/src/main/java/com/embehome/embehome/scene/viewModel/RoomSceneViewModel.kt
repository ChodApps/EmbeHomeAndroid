package com.embehome.embehome.scene.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay


/** com.tronx.tt_things_app.scene.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 08-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RoomSceneViewModel : ViewModel() {



    var action = ""
    val performAction = MutableLiveData (false)
    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    val sceneData  = CacheSceneTwoWay.getSceneData(CacheHubData.selectedMacID)

    fun back () {
        performAction("Back")
    }

    fun add () {
        performAction("Add")
    }
}