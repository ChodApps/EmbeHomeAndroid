package com.embehome.embehome.rules.model


/** com.tronx.tt_things_app.rules.model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 20-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class CloudRuleItemList (val status : String,
                         val message : String,
                         val email : String,
                         val data : ArrayList<RuleItemDetails>
                         )