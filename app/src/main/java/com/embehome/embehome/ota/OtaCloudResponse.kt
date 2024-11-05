package com.embehome.embehome.ota

data class OtaCloudResponse (
    val status : String,
    val message : String,
    val data : OtaDeviceDetails
)