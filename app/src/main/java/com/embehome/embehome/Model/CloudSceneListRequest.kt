package com.embehome.embehome.Model

data class CloudSceneListRequest (
        val status : String,
        val message : String,
        val data : ArrayList<SceneCloudModel>
)