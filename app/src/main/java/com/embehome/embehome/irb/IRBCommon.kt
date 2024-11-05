package com.embehome.embehome.irb

import com.embehome.embehome.R

enum class RemoteCategoryEnum {
    TV, DISH, DVD, SPEAKERS, AC, PROJECTOR,
    TABLE_FAN,
    AIR_PURIFIER,
    COOLER,
    FAN,
    GEYSER,
    OVEN
}


fun getRemoteCatImage (categoryEnum : RemoteCategoryEnum) : Int {
    return when (categoryEnum) {
        RemoteCategoryEnum.AC -> R.drawable.ac
        RemoteCategoryEnum.AIR_PURIFIER -> R.drawable.irb_cat_air_purifier
        RemoteCategoryEnum.COOLER -> R.drawable.irb_cat_cooler
        //RemoteCategoryEnum.CURTAIN -> R.drawable.irb_cat_curtain
        RemoteCategoryEnum.DISH -> R.drawable.irb_cat_dish
        RemoteCategoryEnum.DVD -> R.drawable.irb_cat_dvd
        RemoteCategoryEnum.FAN -> R.drawable.irb_cat_fan
        RemoteCategoryEnum.GEYSER -> R.drawable.irb_cat_geyser
        RemoteCategoryEnum.OVEN -> R.drawable.irb_cat_oven
        RemoteCategoryEnum.SPEAKERS -> R.drawable.irb_cat_speakers
        RemoteCategoryEnum.TABLE_FAN -> R.drawable.irb_cat_table_fan
        RemoteCategoryEnum.TV -> R.drawable.irb_cat_tv
        RemoteCategoryEnum.PROJECTOR -> R.drawable.irb_cat_tv
    }
}

fun getResourceRemoteIcon (type : String) : Int {

    val re = RemoteCategoryEnum.valueOf(type)
    return when (re) {
        RemoteCategoryEnum.TV -> {
            R.drawable.tt_device_enable_tv
        }
        RemoteCategoryEnum.DISH -> {
            R.drawable.tt_device_enable_setupbox
        }
        RemoteCategoryEnum.DVD -> {
            R.drawable.tt_device_enable_dvd
        }
        RemoteCategoryEnum.SPEAKERS -> {
            R.drawable.tt_device_enable_ht_speaker
        }
        RemoteCategoryEnum.AC -> {
            R.drawable.tt_device_enable_ac
        }
        RemoteCategoryEnum.PROJECTOR -> {
            R.drawable.tt_device_enable_projector
        }
        RemoteCategoryEnum.TABLE_FAN -> {
            R.drawable.tt_device_enable_tablefan
        }
        RemoteCategoryEnum.AIR_PURIFIER -> {
            R.drawable.tt_device_enable_airpurifier
        }
        RemoteCategoryEnum.COOLER -> {
            R.drawable.tt_device_enable_cooler
        }
        RemoteCategoryEnum.FAN -> {
            R.drawable.tt_device_enable_fan
        }
        RemoteCategoryEnum.GEYSER -> {
            R.drawable.tt_device_enable_geyser
        }
        RemoteCategoryEnum.OVEN -> {
            R.drawable.tt_device_enable_oven
        }
        else -> {
            R.drawable.tt_device_enable_tv
        }
    }
}

