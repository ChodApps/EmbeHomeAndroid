package com.embehome.embehome.rules.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.databinding.RuleSelectSensorFragmentBinding
import com.embehome.embehome.rules.adapter.ListAllDevicesConditionAdapter
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel

class RuleSelectSensor : Fragment() {

    private val tempViewModel: RuleSelectSensorViewModel by viewModels ()
    val viewModel : RuleTypeSelectionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.ruleDetail.value?.let {
           it.sensor?.let {r ->
               tempViewModel.conditionSwitchStatus.value = r.sensor_status
               tempViewModel.conditionDeviceSno.value = it.sensor.thing_serial_number
               tempViewModel.conditionSwitchId.value = it.sensor.sensor_id
               tempViewModel.conditionDeviceId.value = it.sensor.thing_id
           }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : RuleSelectSensorFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.rule_select_sensor_fragment, container, false)

        val board = CacheHubData.getAllDevices(CacheHubData.selectedMacID).filter {
            it.thing_type != "IRB" && it.thing_type != "CUR"
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
                                        viewModel.setConditionSensorSwitch(requireContext(), tid, sno, sid, ss, l)
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