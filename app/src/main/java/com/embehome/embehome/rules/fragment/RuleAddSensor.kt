package com.embehome.embehome.rules.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.rules.viewModels.RuleAddSensorViewModel
import kotlinx.android.synthetic.main.rule_add_sensor_fragment.view.*

class RuleAddSensor : Fragment() {

    val viewModel: RuleAddSensorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(R.layout.rule_add_sensor_fragment, container, false)

        v.imageView255.setOnClickListener {
            findNavController().navigateUp()
        }

        v.textView203.setOnClickListener {
            findNavController().navigate(R.id.action_ruleAddSensor_to_ruleSelectSensor)
        }


        return v
    }

}