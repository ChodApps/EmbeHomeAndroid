package com.embehome.embehome.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/** com.tronx.tt_things_app.room.entity
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 04-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

@Entity (tableName = "hub_data")
class REHubDetails (@PrimaryKey val macID : String, val name : String, val version : String)