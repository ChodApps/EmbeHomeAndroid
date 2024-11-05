package com.embehome.embehome.Model


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 09-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class PowerUnitModel (
    val status : String,
    val message : String,
    val thing_serial_number : String = "",
    val switchId : Int = 0,
    val area_id : Int = 0,
    val data : PowerData
)

data class PowerData (
    val daily_units : ArrayList<TTPowerUnit> = ArrayList(),
    val weekly_units : ArrayList<TTPowerUnit> = ArrayList(),
    val monthly_units : ArrayList<TTPowerMonthsUnit> = ArrayList()
)

data class TTPowerUnit (
    val time : String,
    val units : Double
)

class TTPowerMonthsUnit (
    val time : String,
    val month : String,
    val year : String,
    val units: Double
)