package com.embehome.embehome.rules.model


/** com.tronx.tt_things_app.rules.model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 20-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RuleDetails (
    val macID : String,
    val rule_id  : Int,
    val rulename_id : Int,
    val rule_type : String,
    val rule_data : RuleDataDetail
)