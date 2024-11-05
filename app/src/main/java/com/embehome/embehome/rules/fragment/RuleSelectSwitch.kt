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
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentRuleSelectSwitchesBinding
import com.embehome.embehome.rules.viewModels.RuleSelectBoardViewModel
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel
import com.embehome.embehome.scene.adapter.SceneBoardExpandListAdapter


/** com.tronx.tt_things_app.rules.fragment
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 14-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RuleSelectSwitch : Fragment() {

    private val viewModel : RuleTypeSelectionViewModel by activityViewModels()
    private val tempViewModel : RuleSelectBoardViewModel by activityViewModels()
    private val twoWay : ArrayList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentRuleSelectSwitchesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rule_select_switches, container, false)
        val board = ArrayList<BoardDetails>()
        tempViewModel.selectedBoard.value?.let {
            it.forEach {thingsId ->
                val b = CacheSceneTwoWay.getBoardData(CacheHubData.selectedMacID, thingsId)
                if (b != null) board.add(b)
            }
            binding.sceneSelectSwitchList.adapter =
                SceneBoardExpandListAdapter(
                    requireContext(),
                    tempViewModel.deviceExpanded,
                    board,
                    tempViewModel.selectedSwitch,
                    twoWay
                ) {

                }
        }

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                when (viewModel.action) {
                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "actionSwitchesSave" -> {
                        tempViewModel.selectedBoard.value?.let { b ->
                            tempViewModel.selectedSwitch.value?.let { s ->
                                viewModel.actionSwitchesSave(requireContext(), s, b, "")
                                findNavController().navigateUp()
                            }
                        }
//                        viewModel.actionSwitchesSave(requireContext(), tempViewModel.selectedSwitch.value, tempViewModel.selectedBoard.value)
                    }

                }
            }
        })


        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}