package com.embehome.embehome.rules.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.embehome.embehome.Model.SceneSwitchDetailModel


/** com.tronx.tt_things_app.rules.viewModels
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 24-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RuleSelectBoardViewModel : ViewModel () {

    val areaExpanded = MutableLiveData (-1)
    val deviceExpanded = MutableLiveData (-1)
    val selectedBoard = MutableLiveData<ArrayList<Int>>(ArrayList())
    val selectedSwitch = MutableLiveData<ArrayList<SceneSwitchDetailModel>>(ArrayList())

}