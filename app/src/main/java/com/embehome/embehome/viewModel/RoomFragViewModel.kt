package com.embehome.embehome.viewModel

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Model.CloudBoardData
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 25-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


@ExperimentalStdlibApi
class RoomFragViewModel : ViewModel() {

    val macID = MutableLiveData<String>()
    val roomFragmentBack = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val boards = MutableLiveData<CloudBoardData>()
    val showToast = MutableLiveData<Boolean>()
    val toastString = MutableLiveData<String>()

    val sceneData  = CacheSceneTwoWay.getSceneData(CacheHubData.selectedMacID)


    val noSceneMessage = MutableLiveData<String>("")


    val gridView = MutableLiveData<Int>()
    val listView = MutableLiveData<Int>()
    val hubInLan = MutableLiveData<Boolean>(false)

    val implRoomData = CacheHubData.getHubData(CacheHubData.selectedMacID)

    init {

        listView.value = View.VISIBLE
        gridView.value = View.GONE

    }


    fun toggleAreaViewType() {
        if (gridView.value == View.VISIBLE) {
            gridView.value = View.GONE
            listView.value = View.VISIBLE
        }
        else {
            listView.value = View.GONE
            gridView.value = View.VISIBLE
        }
    }

    fun roomFragmentBack() {
        roomFragmentBack.value = true
        roomFragmentBack.value = false
    }


    fun displayToast(msg : String) {
        toastString.value = msg
        showToast.value = true
        showToast.value = false
    }

    fun displayHubStatus() {
        if (hubInLan.value == true)
            displayToast("Hub Found in Connected Network")
        else
            displayToast("Hub Not Found In the Connected Network")
    }

    fun loadImages (context : Context, areaID : ArrayList<Int>) = viewModelScope.launch {
        var flagToRefresh = false
        areaID.forEach {
            val area = CacheHubData.getArea(it)
            if (area.image == null) {
                val res = AppServices.imageSave(context, area.area_image)
                if (res != null && area.image == null) {
                    area.image = res
                    flagToRefresh = true
                }
            }
        }
        if (flagToRefresh) implRoomData.value = implRoomData.value
    }

}