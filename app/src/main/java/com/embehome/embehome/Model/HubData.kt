package com.embehome.embehome.Model

import androidx.lifecycle.MutableLiveData


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 19-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class HubData (val macID : String, var name : String, var version : String, val ip : MutableLiveData<String>, var image : String = "", var bssid : String = "")