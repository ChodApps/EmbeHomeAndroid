package com.embehome.embehome.notification.model


/** com.tronx.things.notification.model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 26-08-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


data class NotificationPrefCloudModel (
    val status : String,
    val message : String,
    val data : NotificationPref?
)

data class NotificationPref (
    val fcm_enabled : Boolean,
    val fcm_thing_status : Boolean,
    val fcm_alert_status : Boolean
)