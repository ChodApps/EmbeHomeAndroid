package com.embehome.embehome.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scene_item")
class RESceneItem (@PrimaryKey val sceneItemID : Int, val sceneItemName : String, val sceneItemImage : String, val sceneDefault : Boolean)