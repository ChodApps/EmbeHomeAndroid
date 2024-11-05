package com.embehome.embehome.Model

import android.graphics.Bitmap


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 05-03-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class AreaData (val area_id : Int, var area_name : String, val area_image : String, val default : Boolean, var image : Bitmap? = null)