package com.embehome.embehome.rules


/** com.tronx.tt_things_app.rules
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 14-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


enum class RuleCondition {
    EVENT, TIMER, COUNTER
}

enum class RuleSelection {
    SWITCHES, SCENES
}

fun getWeek(day : Int) : String{
    return when (day) {
        0 -> "S"
        1 -> "M"
        2 -> "T"
        3 -> "W"
        4 -> "T"
        5 -> "F"
        6 -> "S"
        else -> "E"
    }
}