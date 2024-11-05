package com.embehome.embehome.irb.model

data class RemoteCloudModel (
        val macID : String,
        val thing_serial_number : String,
        val thing_id : Int,
        val remote_name : String,
        val remote_id : Int,
        val ir_category : String,
        val ir_model : String,
        val ir_data : HashMap<String,String>
)