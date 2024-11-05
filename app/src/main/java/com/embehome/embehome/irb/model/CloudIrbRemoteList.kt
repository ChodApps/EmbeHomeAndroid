package com.embehome.embehome.irb.model


/** com.tronx.tt_things_app.irb.model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 01-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class CloudIrbRemoteList (val status : String, val message : String, val data : ArrayList<RemoteCloudModel>)