package com.embehome.embehome.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/** com.tronx.tt_things_app.room.entity
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 10-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

@Entity(tableName = "two_way")
class RETwoWay (@PrimaryKey val macID : String,
                val twoway_id : Int,
                val twoway_name : String,
                val device_details : String
)