package com.embehome.embehome.ota

data class OtaDeviceDetails (
    val module_type : String,
    val latest_version : String,
    val update_available : Boolean,
    val download_path : String?,
    val file_size : String?
        )