package com.embehome.embehome.Model


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 26-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class BoardDetails (val thing_serial_number : String,
                         val thing_type : String,
                         val category : String,
                         var area_id : Int,
                         var thing_name : String,
                         val thing_image : String,
                         val thing_id : Int,
                         val switchesList : ArrayList<SwitchDetails>,
                         var thing_version : String? = null,
                         var touch_sensitivity : String? = null
) {
    fun replicate (obj : BoardDetails) {
        switchesList.clear()
        switchesList.addAll(obj.switchesList)
    }
}