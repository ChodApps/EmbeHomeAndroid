package com.embehome.embehome.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.embehome.embehome.room.entity.RETwoWay


/** com.tronx.tt_things_app.room.dao
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 10-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

@Dao
interface RDTwoWay {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTwoWay (twoWay : RETwoWay)

    @Query ("select * from two_way where macID = :macID")
    fun getTwoList (macID : String) : List<RETwoWay>

    @Query ("delete from two_way where twoway_id = :id")
    fun deleteTwoWay (id : Int)

    @Query ("Delete from two_way")
    fun deleteAllTwoWay ()

}