package com.embehome.embehome.rules

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.embehome.embehome.R

class AddRule : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_rule)
        findNavController(R.id.fragment6).setGraph(R.navigation.rule_add, intent.extras)
    }
}