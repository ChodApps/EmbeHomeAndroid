package com.embehome.embehome.Model

data class CloudSceneItemList (
        val status : String,
        val message : String,
        val email : String,
        val data : ArrayList<SceneItemModel>
)