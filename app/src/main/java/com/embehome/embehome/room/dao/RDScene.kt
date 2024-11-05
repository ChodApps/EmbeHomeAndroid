package com.embehome.embehome.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.embehome.embehome.room.entity.REScene


/** com.tronx.tt_things_app.room.dao
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 10-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

@Dao
interface RDScene {

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    fun insertScene (scene : REScene)

    @Query ("select * from scenes where macID = :macID")
    fun getScenes (macID : String) : List<REScene>

    @Query ("delete from scenes where scene_id = :sceneID")
    fun deleteScene (sceneID : Int)

    @Query ("delete from scenes where macID = :macID")
    fun deleteAllSceneForMacID (macID: String)

    @Query ("delete from scenes")
    fun deleteAllScenes()
}