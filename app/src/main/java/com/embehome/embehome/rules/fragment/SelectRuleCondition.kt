package com.embehome.embehome.rules.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.rules.viewModels.SelectRuleConditionViewModel
import kotlinx.android.synthetic.main.select_rule_condition_fragment.view.*

class SelectRuleCondition : Fragment() {

    val  viewModel: SelectRuleConditionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(R.layout.select_rule_condition_fragment, container, false)

        v.imageView255.setOnClickListener {
            findNavController().navigateUp()
        }

        v.textView203.setOnClickListener {
            findNavController().navigate(R.id.action_selectRuleCondition_to_ruleAddTime)
        }

        v.textView204.setOnClickListener {
            findNavController().navigate(R.id.action_selectRuleCondition_to_ruleAddSensor)
        }

        v.textView205.setOnClickListener {
            findNavController().navigate(R.id.action_selectRuleCondition_to_ruleAddCounter)
        }


        return v
    }
}