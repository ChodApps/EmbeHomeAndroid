package com.embehome.embehome.viewModel

import androidx.lifecycle.MutableLiveData
import com.embehome.embehome.BaseViewModel

class HubActivityViewModel : BaseViewModel () {

    val displayBottomBar = MutableLiveData(true);

    fun changeBottomBarDisplay (status : Boolean) {
        displayBottomBar.value = status
    }


}