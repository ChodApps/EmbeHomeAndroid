package com.embehome.embehome.Model


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 21-05-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class SwitchAlertData (
    val alert_id : Int,
    val switchId : Int,
    val repeat_alert : Int,
    val switchstatus : Int,
    val time_interval : Int,
    val thing_serial_number : String
)