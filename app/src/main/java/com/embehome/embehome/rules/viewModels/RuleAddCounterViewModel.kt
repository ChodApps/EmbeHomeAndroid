package com.embehome.embehome.rules.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RuleAddCounterViewModel : ViewModel() {
    val counterTime = MutableLiveData<String>()
}