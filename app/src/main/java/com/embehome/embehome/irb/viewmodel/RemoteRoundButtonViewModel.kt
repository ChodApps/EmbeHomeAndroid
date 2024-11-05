package com.embehome.embehome.irb.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/** com.tronx.tt_things_app.irb.viewmodel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 29-05-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RemoteRoundButtonViewModel : ViewModel() {

    val performAction = MutableLiveData (false)
    var action = ""

    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    fun btnWEB() {
        performAction("Clicked WEB")
    }

    fun btnSTOP() {
        performAction("Clicked STOP")
    }

    fun btnPLAY() {
        performAction("Clicked PLAY")
    }

    fun btnPAUSE() {
        performAction("Clicked PAUSE")
    }

    fun btnINFO() {
        performAction("Clicked INFO")
    }

    fun btnSETTING() {
        performAction("Clicked SETTINGS")
    }

    fun btnDOWN() {
        performAction("Clicked DOWN")
    }

    fun btnOK() {
        performAction("Clicked OK")
    }

    fun btnLEFT() {
        performAction("Clicked LEFT")
    }

    fun btnRIGHT() {
        performAction("Clicked RIGHT")
    }

    fun btnUP() {
        performAction("Clicked UP")
    }



}