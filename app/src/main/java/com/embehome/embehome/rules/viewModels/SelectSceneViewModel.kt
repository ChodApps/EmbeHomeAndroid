package com.embehome.embehome.rules.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectSceneViewModel : ViewModel() {
    val selectedSceneID = MutableLiveData<ArrayList<Int>> (ArrayList())
}