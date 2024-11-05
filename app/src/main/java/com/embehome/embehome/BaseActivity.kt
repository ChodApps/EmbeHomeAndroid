package com.embehome.embehome

import androidx.appcompat.app.AppCompatActivity
import com.embehome.embehome.Utils.AppDialogs


/** com.tronx.things
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 22-12-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


abstract class BaseActivity : AppCompatActivity() {


    override fun onDestroy() {
        super.onDestroy()

        AppDialogs.stopLoadScreen()
    }
}