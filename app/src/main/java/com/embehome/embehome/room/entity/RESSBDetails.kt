package com.embehome.embehome.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/** com.tronx.tt_things_app.room.entity
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 04-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

@Entity (tableName = "ssb_details")
class RESSBDetails (@PrimaryKey val serialNo : String, val type : String, val category : String, val area_id : Int, val name : String, val image : String, val thingsID : Int, val switches : String, val macId : String)