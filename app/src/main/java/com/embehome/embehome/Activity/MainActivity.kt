package com.embehome.embehome.Activity

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices


class MainActivity : AppCompatActivity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppServices.openLogFile(this)

        AppServices.permissionActivity(this)
        AppServices.exactAlarmPermissionActivity(this)

        onBackPressedDispatcher.addCallback(this){
            findNavController(R.id.auth_host).navigateUp()
        }
    }

}




