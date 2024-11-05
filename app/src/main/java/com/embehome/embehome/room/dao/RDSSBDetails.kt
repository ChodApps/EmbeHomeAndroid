package com.embehome.embehome.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.embehome.embehome.room.entity.RESSBDetails


/** com.tronx.tt_things_app.room.dao
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 04-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

@Dao
interface RDSSBDetails {

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBoardData (board : RESSBDetails)

    @Query ("Select * from ssb_details")
    fun getAllBoardData () : List<RESSBDetails>

    @Query ("select * from ssb_details where macId = :macId and area_id = :areaId")
    fun getAreaBoardData (macId : String, areaId : Int) : List<RESSBDetails>

    @Query ("Select * from ssb_details where serialNo = :sno")
    fun getBoardData (sno: String) : RESSBDetails

    @Query ("Delete from ssb_details")
    fun deleteAllBoards ()

    @Query ("Delete from ssb_details where thingsID = :thingsID")
    fun deleteBoard (thingsID : Int)

}