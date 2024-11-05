package com.embehome.embehome.rules.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.databinding.RuleAddCounterFragmentBinding
import com.embehome.embehome.rules.RuleCondition
import com.embehome.embehome.rules.viewModels.RuleAddCounterViewModel
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel

class RuleAddCounter : Fragment() {

    val tempViewModel: RuleAddCounterViewModel by viewModels ()
    val viewModel: RuleTypeSelectionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.condition.value == RuleCondition.COUNTER) {
            viewModel.ruleDetail.value?.let {
                tempViewModel.counterTime.value = it.counter?.stop_counter.toString()
            }
        }
//        tempViewModel.counterTime.value = viewModel.counterTime.value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : RuleAddCounterFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.rule_add_counter_fragment, container, false)

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                when (viewModel.action) {
                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "ConditionCounterDone" -> {
                        tempViewModel.counterTime.value?.let { time ->
                            if (time.isNotEmpty()) {
                                viewModel.setConditionCounter(time)
                                findNavController().navigateUp()
                            }

                        }
                    }

                    else -> {

                    }
                }
            }
        })




        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.localViewModel = tempViewModel
        return binding.root
    }

}