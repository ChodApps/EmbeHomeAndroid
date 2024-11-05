package com.embehome.embehome.rules.model

import com.embehome.embehome.Model.SceneSwitchDetailModel


/** com.tronx.tt_things_app.rules.model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 20-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class RuleDataDetail (
    val rule_action : RuleDeviceDataDetail,
    val rule_details : RuleTypeDetail
)

data class RuleTypeDetail (
    val timer : RuleTimer?,
    val sensor : RuleSensor?,
    val counter : RuleConditionCounter?

)

data class RuleTimer (
    val weekdays : ArrayList<Int> = ArrayList(),
    val time : Int,
    val repeat : Boolean
)

data class RuleSensor (
    val sensor_type : String,
    val sensor_condition : String,
    val sensor_id : Int,
    val sensor_status : Int,
    val thing_serial_number : String,
    val thing_id : Int
)

data class RuleConditionCounter (
    val stop_counter : Int
)

class RuleDeviceDataDetail (
    val device_list : ArrayList<SceneSwitchDetailModel> = ArrayList(),
    val scenes_list : ArrayList<Int> = ArrayList()
)

data class DeviceDetail (
    val thing_id : Int,
    val thing_serial_number : String,
    val switchId : Int,
    val switchstatus : Int
)