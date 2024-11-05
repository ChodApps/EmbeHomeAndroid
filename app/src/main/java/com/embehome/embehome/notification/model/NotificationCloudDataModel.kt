package com.embehome.embehome.notification.model

data class NotificationCloudDataModel (
        val notification_id : Int,
        val fcm_notification : FCMNotification,
        val createdAt : String
)