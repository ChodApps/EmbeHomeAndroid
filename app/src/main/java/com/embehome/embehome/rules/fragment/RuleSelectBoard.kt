package com.embehome.embehome.rules.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.databinding.FragmentRuleSelectBoardBinding
import com.embehome.embehome.rules.viewModels.RuleSelectBoardViewModel
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel
import com.embehome.embehome.scene.adapter.SceneExpandableListAdapter


/** com.tronx.tt_things_app.rules.fragment
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 14-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RuleSelectBoard : Fragment() {

    private val tempViewModel : RuleSelectBoardViewModel by activityViewModels()
    val viewModel : RuleTypeSelectionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.selectedSwitch.value.let {
            tempViewModel.selectedSwitch.value?.clear()
            if (it != null && it.size > 0)
            tempViewModel.selectedSwitch.value?.addAll(it)
        }
        viewModel.selectedBoard.value.let {
            tempViewModel.selectedBoard.value?.clear()
            if (it != null && it.size > 0)
                tempViewModel.selectedBoard.value?.addAll(it)
        }
        tempViewModel.areaExpanded.value = -1
        tempViewModel.deviceExpanded.value = -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentRuleSelectBoardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rule_select_board, container, false)

        val data = CacheHubData.getHubData(CacheHubData.selectedMacID)
        binding.expandableBoardList.adapter =
            SceneExpandableListAdapter(
                requireContext(),
                tempViewModel.areaExpanded,
                data,
                tempViewModel.selectedBoard
            ) {

            }

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                when (viewModel.action) {
                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "Continue" -> {
                        tempViewModel.selectedBoard.value?.let {
                            if (it.size > 0) findNavController().navigate(R.id.action_ruleSelectBoard_to_ruleSelectSwitch)
                        }

                    }
                }
            }
        })


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }
}