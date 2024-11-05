package com.embehome.embehome.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.embehome.embehome.room.entity.AreaDetails

@Dao
interface AreaDao {
    @Query("SELECT * from area_details")
    fun getAlphabetizedWords(): List<AreaDetails>

    @Query("SELECT * from area_details where area_id = :id")
    fun getArea(id : Int): AreaDetails?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(area: AreaDetails)

    @Query("DELETE FROM area_details")
    suspend fun deleteAll()
}