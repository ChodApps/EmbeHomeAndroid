package com.embehome.embehome.Model


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 06-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class HubCloudData (
        val email : String,
        val macID : String,
        val hub_name : String,
        val hub_version : String,
        val wifi_SSID : String,
        val hub_image : String,
        val hub_custom_image : String?,
        val serial_number : String?,
        val pan_ID : String?,
        val createdAt : String,
        val updatedAt : String
    )