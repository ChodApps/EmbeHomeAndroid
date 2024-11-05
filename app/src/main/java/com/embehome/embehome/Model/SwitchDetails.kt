package com.embehome.embehome.Model


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 26-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class SwitchDetails (val switchId : Int,
                          var switchName : String,
                          val switchType : String,
                          var switchstatus : Int,
                          var switchLevel : Int,
                          var switchIconId : String,
                          val scenesList : ArrayList<Int>?,
                          var twoway_id : String?,
                          var alert_data : SwitchAlertData?,
                          var switchWattage : Int?
)