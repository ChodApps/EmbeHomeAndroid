package com.embehome.embehome.Model


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 22-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class CloudSetAlertModel (
    val macID : String,
    val thing_serial_number : String,
    val switchId : Int,
    val switchstatus : Int,
    val time_interval : Int,
    val alert_timestamp : String,
    val repeat_alert : Int
)