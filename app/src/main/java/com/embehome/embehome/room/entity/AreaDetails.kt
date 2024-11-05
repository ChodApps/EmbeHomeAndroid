package com.embehome.embehome.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "area_details")
class AreaDetails (
        @PrimaryKey @ColumnInfo (name = "area_id") val area_id : Int,
        @ColumnInfo (name = "area_name")val area_name : String,
        @ColumnInfo (name = "area_image")val area_image : String,
        @ColumnInfo (name = "area_default")val areaDefault : Boolean
)