package com.embehome.embehome.rules.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Model.SceneSwitchDetailModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.databinding.RuleSelectSensorFragmentBinding
import com.embehome.embehome.rules.RuleCondition
import com.embehome.embehome.rules.adapter.ListAllDevicesConditionAdapter
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel
import java.lang.Exception


/** com.tronx.things.rules.fragment
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 24-08-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class RuleSelectSingleSwitch : Fragment() {

    private val tempViewModel: RuleSelectSensorViewModel by viewModels ()
    private val viewModel : RuleTypeSelectionViewModel by activityViewModels()
    private var sensorThingExcluded = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.ruleAction.value?.let {
            if (it.device_list.size == 1) {
                tempViewModel.conditionDeviceId.value = it.device_list[0].thing_id
                tempViewModel.conditionDeviceSno.value = it.device_list[0].thing_serial_number
                tempViewModel.conditionSwitchId.value = it.device_list[0].switchId
                tempViewModel.conditionSwitchStatus.value = it.device_list[0].switchstatus
            }
        }

        viewModel.ruleDetail.value?.let {
            it.sensor?.let {
                sensorThingExcluded = it.thing_id
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : RuleSelectSensorFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.rule_select_sensor_fragment, container, false)

        var board = CacheHubData.getAllDevices(CacheHubData.selectedMacID).filter {
            it.thing_type != "IRB" && sensorThingExcluded != it.thing_id
        }

        try {
            viewModel.condition.value?.let {
                if (it == RuleCondition.COUNTER)
                    board = board.filter {
                        it.thing_type != "CUR"
                    }
            }
        }catch (e : Exception) {
            Log.e("TronX", "rule curtain counter exception - ${e.message}")
        }

        if (board.isEmpty()) {
            Toast.makeText(requireContext(), "Device's not available", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        binding.recyclerViewSwitches.adapter = ListAllDevicesConditionAdapter (
            requireContext(),
            MutableLiveData(),
            ArrayList(board),
            tempViewModel.conditionSwitchId,
            tempViewModel.conditionDeviceId,
            tempViewModel.conditionSwitchStatus,
            tempViewModel.conditionDeviceSno
        )

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                when (viewModel.action){
                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "setConditionSensorSwitch" -> {
                        tempViewModel.conditionDeviceId.value?.let { tid ->
                            tempViewModel.conditionDeviceSno.value?.let { sno ->
                                tempViewModel.conditionSwitchId.value?.let { sid ->
                                    tempViewModel.conditionSwitchStatus.value?.let {ss ->
//                                        viewModel.setConditionSensorSwitch(requireContext(), tid, sno, sid, ss)
                                        var l = ""
                                        board.filter {
                                            it.thing_id == tid
                                        }.also {b ->
                                            if (b.isNotEmpty()) {
                                                b[0].switchesList.filter {
                                                    it.switchId == sid
                                                }.also {
                                                    if (it.isNotEmpty()) {
                                                        l = "${b[0].thing_name} - ${it[0].switchName} : ${if (ss == 0) "Off" else "On"}"
                                                    }
                                                }
                                            }
                                        }
                                        val a = ArrayList<SceneSwitchDetailModel>().apply {
                                            add(SceneSwitchDetailModel(tid, sno, sid, ss))
                                        }
                                        viewModel.actionSwitchesSave(requireContext(), a, ArrayList(), l)
                                        findNavController().navigateUp()
                                    }
                                }
                            }
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