package com.embehome.embehome.irb.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/** com.tronx.tt_things_app.irb.viewmodel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 19-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RemoteProjectorViewModel : ViewModel(){

    val performAction = MutableLiveData (false)
    var thingsID = 0
    var macID = ""
    var sno = ""
    var remoteID = 0
    val remoteName = MutableLiveData ("TV")
    var action = ""

    val editMode = MutableLiveData(View.GONE)
    var operationType  = ""

    var remoteIRData = getIrData()

    fun getIrData () : HashMap<String,String> {


        return HashMap()
    }

    fun performAction (data : String) {
        action = data
        performAction.value = true
        performAction.value = false
    }

    fun back () {
        performAction("Back")
    }

    fun setEditMode (operationType : String) {
        when (operationType) {
            "Create" -> {
                editMode.value = View.VISIBLE
            }

            "Operate" -> {
                editMode.value = View.GONE
            }
        }
    }


}