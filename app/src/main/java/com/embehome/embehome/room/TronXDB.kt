package com.embehome.embehome.room.db.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.embehome.embehome.room.dao.AreaDao
import com.embehome.embehome.room.dao.RDHubDetails
import com.embehome.embehome.room.dao.RDSSBDetails
import com.embehome.embehome.room.dao.RDSceneItem
import com.embehome.embehome.room.entity.*

@Database (entities = [AreaDetails::class, RESceneItem::class, REHubDetails::class, RESSBDetails::class, RETwoWay::class, REScene::class], version = 1, exportSchema = false)
abstract class TronXDB : RoomDatabase () {
    abstract fun areaDao() : AreaDao
    abstract fun sceneDao () : RDSceneItem
    abstract fun hubDao () : RDHubDetails
    abstract fun ssbDao () : RDSSBDetails

    companion object {
        @Volatile
        private  var INSTANCE : TronXDB? = null
        fun getDB (context: Context): TronXDB? {
            val dbInstance = INSTANCE
            if (dbInstance != null)
                return INSTANCE

            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        TronXDB::class.java,
                        "tronxDB"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}