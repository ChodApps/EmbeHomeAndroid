package com.embehome.embehome.Model

data class TwoWayItemModel (
        val macID : String,
        val twoway_id : Int,
        val twoway_name : String,
        val device_details : TwoWaySwitchDetails
)