package com.embehome.embehome.rules.model

import android.graphics.Bitmap


/** com.tronx.tt_things_app.rules.model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 20-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class RuleItemDetails (val rulename_id : Int, var rule_name : String, val rule_image : String, val default_rule : Boolean, var image : Bitmap? = null)