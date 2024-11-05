package com.embehome.embehome.Model

import android.graphics.Bitmap

data class SceneItemModel (val scenename_id : Int,
                           var scene_name : String,
                            val scene_image : String,
                            val default : Boolean,
                           var image : Bitmap? = null
                           )