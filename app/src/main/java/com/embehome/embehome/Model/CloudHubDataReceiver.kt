package com.embehome.embehome.Model


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 06-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class CloudHubDataReceiver (val status : String, val message : String, val data : HashMap<String,String>)