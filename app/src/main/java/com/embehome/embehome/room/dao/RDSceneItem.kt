package com.embehome.embehome.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.embehome.embehome.room.entity.RESceneItem

@Dao
interface RDSceneItem {

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSceneItem (sceneItem : RESceneItem)

    @Query ("Select * from scene_item")
    fun getSceneItemList () : List<RESceneItem?>

}