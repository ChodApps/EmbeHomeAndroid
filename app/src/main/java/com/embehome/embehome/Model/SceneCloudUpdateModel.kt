package com.embehome.embehome.Model

import com.embehome.embehome.scene.IRSceneModel

data class SceneCloudUpdateModel (
        val macID : String,
        val scene_id_old : Int,
        val scene_id_new : Int,
        val scenename_id : Int,
        val device_list : ArrayList<SceneSwitchDetailModel>,
        val ir_data : ArrayList<IRSceneModel>,
        val subscene_list : ArrayList<Int> = ArrayList()
)