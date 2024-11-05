package com.embehome.embehome

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.HubData
import com.embehome.embehome.Model.SwitchDetails
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.room.entity.REHubDetails
import com.embehome.embehome.room.entity.RESSBDetails


/** com.tronx.tt_things_app
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 04-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

data class switchDaoModel(val data : ArrayList<SwitchDetails>)

val CHANNEL_ID = "STATUS"

val notificationReceived = MutableLiveData<String>("")

fun registerNotification (owner : LifecycleOwner, context: Context) {
    val bell =  MediaPlayer.create(context, R.raw.bell)
    notificationReceived.observe(owner, Observer {
        if (it != null &&  it.length > 5) {

            try {

            if (it == "Bell alert\nSomeone is ringing the bell!") {
                bell.start()
                AppServices.toastShort(context, it)
            }else if (it == "Bell alert \n" +
                " Someone is ringing the bell!"){

            }
            else
            AppServices.toastShort(context, it)
            notificationReceived.value = ""
            }
            catch (e : Exception) {
                AppServices.log("TronX_Notify", "Notification Display - ${e.message}")
            }
        }
    })
}

fun deRegisterNotification (owner : LifecycleOwner) {
    notificationReceived.removeObservers(owner)
}

fun getHUBFromDao (hub : REHubDetails) = HubData(hub.macID, hub.name, hub.version, MutableLiveData())

fun getDaoFromHub (hub : HubData) = REHubDetails(hub.macID, hub.name, hub.version)

fun getDeviceFromDao ( device : RESSBDetails) : BoardDetails{
    return if (device.switches.length > 5) {
        try {
            val a  = Gson().fromJson(device.switches, switchDaoModel::class.java)
            BoardDetails (device.serialNo, device.type, device.category, device.area_id, device.name, device.image, device.thingsID,
                a?.data ?: ArrayList()
            )
        } catch (e: Exception) {
            AppServices.log(e.message.toString())
            AppServices.log("TronX" , e.stackTrace.toList().toString())
            BoardDetails (device.serialNo, device.type, device.category, device.area_id, device.name, device.image, device.thingsID, ArrayList())
        }
    } else
    BoardDetails (device.serialNo, device.type, device.category, device.area_id, device.name, device.image, device.thingsID, ArrayList())
}

fun getDaoFromDevice (device : BoardDetails, macId : String) : RESSBDetails {

    val a = Gson().toJson(switchDaoModel(device.switchesList))
    return RESSBDetails(device.thing_serial_number, device.thing_type, device.category, device.area_id, device.thing_name, device.thing_image, device.thing_id, a ?: "", macId)

}

fun getHubImage (name : String) : Int {
    return when (name) {
        "hub_icon1" -> {
            R.drawable.hub_icon1
        }

        "hub_icon2" -> {
            R.drawable.hub_icon2
        }

        "hub_icon3" -> {
            R.drawable.hub_icon3
        }
        "hub_icon4" -> {
            R.drawable.hub_icon4
        }
        "hub_icon5" -> {
            R.drawable.hub_icon5
        }
        "hub_icon6" -> {
            R.drawable.hub_icon6
        }
        "hub_icon7" -> {
            R.drawable.hub_icon7
        }

        "hub_icon8" -> {
            R.drawable.hub_icon8
        }
        "hub_icon9" -> {
            R.drawable.hub_icon9
        }

        "hub_icon10" -> {
            R.drawable.hub_icon10
        }

        "hub_icon11" -> {
            R.drawable.hub_icon11
        }

        "hub_icon12" -> {
            R.drawable.hub_icon12
        }

        "hub_icon13" -> {
            R.drawable.hub_icon13
        }

        else -> {
            R.drawable.hub_icon1
        }

    }
}

class DialerDimmer(context : Context) : View(context) {

}

fun getDeviceType (type : String) : String {
    return when (type) {
        "SSB" ->{
            "Smart Switch Board"
        }
        "RSB" -> {
            "Retrofit Switch Board"
        }
        "IRB" -> {
            "IR Blaster"
        }
        "CUR" -> {
            "Curtain"
        }
        else -> {
            "Device"
        }
    }
}

fun getSwitchImage (switchType : String, status : Int = 0) : Int {
    return when (switchType) {
        "A" -> {
            if (status == 0)
                R.drawable.bulb_off
            else
                R.drawable.bulb_on
        }

        "B" -> {
            if (status == 0)
                R.drawable.tt_device_disable_ceilingfan
            else
                R.drawable.tt_device_enable_ceilingfan

        }
        "C" -> R.drawable.tt_device_disable_led
        else -> {
            R.drawable.bulb_off
        }
    }
}

//fun getDimmerAngle (w : Int, h :Int, x : Float, y : Float) : Int? {
//    val p : Float = (70 / 300f * w).toFloat()
//    val aa = w/2f - p
//    val bb = w/2f + p
//    val cc = (bb - aa) / 11f
//    if (x in aa..bb && y in aa..w/2f) {
//        val ff = ((x - aa) / cc).toInt()
//        val angle = ((180 / 10 * ff) + 270) % 360
//        return angle
//    }
////        val f = x / w * 130
//////        if (p > 85 && p < 215) {
//////           p = p - 85
//////            p = 270
//////        }
////        val a = 270 + (f / p * 180) % 360
//    return  null// if (a > 90 && a < 270) 90f else if( a > 180 && a < 270) 270f else a
//}
//
//
//fun getLevel (angle : Int) {
//
//}
//
//fun getAngle (level : Int) {
//
//}


enum class HubImageNames {
    hub_icon1, hub_icon2, hub_icon3, hub_icon4, hub_icon5, hub_icon6, hub_icon7, hub_icon8, hub_icon9, hub_icon10, hub_icon11, hub_icon12, hub_icon13
}

enum class SwitchImageNames {
    switch_icon1, switch_icon2, switch_icon3, switch_icon4, switch_icon5, switch_icon6, switch_icon7 //, switch_icon8
}

fun getSwitchImages  (img : String, status : Boolean) : Int {
    return when (img) {
        "switch_icon1" -> {
            if (status) R.drawable.switchicon_1
            else R.drawable.switchicon_0
        }
        "switch_icon2" -> {
            if (status) R.drawable.tt_device_enable_ceilingfan
            else R.drawable.tt_device_disable_ceilingfan
        }

        "switch_icon3" -> {
            if (status) R.drawable.tt_device_enable_cfl
            else R.drawable.tt_device_disable_cfl
        }

        "switch_icon4" -> {
            if (status) R.drawable.tt_device_enable_tubelight
            else R.drawable.tt_device_disable_tubelight
        }

        "switch_icon5" -> {
            if (status) R.drawable.tt_device_enable_ac
            else R.drawable.tt_device_disable_ac
        }

        "switch_icon6" -> {
            if (status) R.drawable.tt_device_enable_tablefan
            else R.drawable.tt_device_disable_tablefan

        }

        "switch_icon7" -> {
            if (status) R.drawable.tt_device_enable_tv
            else R.drawable.tt_device_disable_tv
        }

        "switch_icon8" -> {
            R.drawable.bottom_navigation_remote
        }

        "switch_icon9" -> {
            R.drawable.switch_icon9
        }

        else -> {
            R.drawable.tt_device_disable_bulb
        }
    }
}

fun getWeekDay (week : Int) : String = when (week) {
    0 -> "Sun"
    1 -> "Mon"
    2 -> "TUE"
    3 -> "WED"
    4 -> "THU"
    5 -> "FRI"
    6 -> "SAT"
    else ->  "Sun"
}
