package com.embehome.embehome.notification.model

data class NotificationCloudModel (
        val status : String,
        val message : String,
        val data : NotificationCloudInternalModel
)

data class NotificationCloudInternalModel (
        val email : String,
        val notifications_count : Int,
        val notifications_data : ArrayList<NotificationCloudDataModel>
)