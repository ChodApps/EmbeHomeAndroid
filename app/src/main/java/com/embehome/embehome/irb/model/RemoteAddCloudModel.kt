package com.embehome.embehome.irb.model


/** com.tronx.things.irb.model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 19-10-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class RemoteAddCloudModel (
    val status : String,
    val message : String,
    val data : RemoteCloudModel
)