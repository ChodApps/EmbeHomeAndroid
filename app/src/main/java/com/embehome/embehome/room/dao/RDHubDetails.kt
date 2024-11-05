package com.embehome.embehome.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.embehome.embehome.room.entity.REHubDetails


/** com.tronx.tt_things_app.room.dao
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 04-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

@Dao
interface RDHubDetails {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHubData (hub : REHubDetails)

    @Query("Select * from hub_data")
    fun getHubList () : List<REHubDetails?>

    @Query("delete from hub_data where macID = :macID")
    fun deleteHub (macID : String)

    @Query("delete from hub_data")
    fun deleteAllHub ()
}