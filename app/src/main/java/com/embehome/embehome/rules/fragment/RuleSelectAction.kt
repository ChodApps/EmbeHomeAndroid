package com.embehome.embehome.rules.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.rules.RuleCondition
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel
import kotlinx.android.synthetic.main.rule_select_action_fragment.view.*

class RuleSelectAction : Fragment() {

//    val  viewModel: RuleSelectActionViewModel by viewModels ()
    val viewModel: RuleTypeSelectionViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(R.layout.rule_select_action_fragment, container, false)

        viewModel.condition.value?.let {
            if (it == RuleCondition.COUNTER) v.textView204.visibility = View.INVISIBLE
        }

        v.imageView255.setOnClickListener {
            findNavController().navigateUp()
        }

        v.textView203.setOnClickListener {
//            findNavController().navigate(R.id.action_ruleSelectAction_to_ruleSelectBoard)
            findNavController().navigate(R.id.action_ruleSelectAction_to_ruleSelectSingleSwitch)
        }

        v.textView204.setOnClickListener {
                findNavController().navigate(R.id.action_ruleSelectAction_to_selectScene)
        }

        return v
    }

}