package com.embehome.embehome.Model

import com.embehome.embehome.scene.IRSceneModel

data class SceneCloudModel (
        val macID : String,
        val scene_id : Int,
        val scenename_id : Int,
        val device_list : ArrayList<SceneSwitchDetailModel> = ArrayList(),
        val ir_data : ArrayList<IRSceneModel>  = ArrayList(),
        val subscene_list : ArrayList<Int> = ArrayList()
)