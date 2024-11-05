package com.embehome.embehome.Model

data class TwoWaySwitchDetails (
    val pri_switchId : Int,
    val pri_thing_id : Int,
    val sec_switchId : Int,
    val sec_thing_id : Int,
    val pri_thing_serial_number : String,
    val sec_thing_serial_number : String
)