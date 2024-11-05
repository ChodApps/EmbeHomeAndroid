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
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.RuleTypeSelectionFragmentBinding
import com.embehome.embehome.rules.RuleCondition
import com.embehome.embehome.rules.adapter.SelectRuleItemAdapter
import com.embehome.embehome.rules.utils.RuleDataRepository
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel

class RuleTypeSelection : Fragment() {


    val viewModel: RuleTypeSelectionViewModel by activityViewModels()
    val args : RuleTypeSelectionArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.ruleId > 0) {

            val rule = RuleDataRepository.getRule(CacheHubData.selectedMacID, args.ruleId)
            rule?.let {
                viewModel.updateView(viewModel.ruleCreateView)
                val ruleItem = RuleDataRepository.getRuleItem(it.rulename_id)
                viewModel.ruleName.value = ruleItem.rule_name
                viewModel.ruleNameId.value = ruleItem.rulename_id
                viewModel.ruleDetail.value = it.rule_data.rule_details.also {r->
                    when (RuleCondition.valueOf(it.rule_type)) {
                        RuleCondition.COUNTER -> {
                            viewModel.ruleConditionDisplay.value = "Counter - ${(r.counter?.stop_counter?.toInt()?: 0) / 60} : ${(r.counter?.stop_counter?.toInt()?: 0) % 60}"
                        }
                        RuleCondition.EVENT -> {
                            viewModel.ruleConditionDisplay.value = "Sensor"
                            try {
                                r.sensor?.let {sensor ->
                                    CacheSceneTwoWay.getBoardData(CacheHubData.selectedMacID, sensor.thing_id)?.let {b ->
                                        b.switchesList.filter {
                                            it.switchId == sensor.sensor_id
                                        }.let {
                                          if (it.isNotEmpty()) {
                                              val t = "${b.thing_name} - ${it[0].switchName} : ${if (sensor.sensor_status == 0) "Off" else "On"}"
                                              viewModel.ruleConditionDisplay.value = t
                                          }
                                        }
                                    }
                                }
                            }
                            catch (e : Exception) {
                                AppServices.log("Tronx", "Rule Edit - ${e.message}")
                            }
                        }
                        RuleCondition.TIMER ->  {
                            r.timer?.let {
                                var d = ""
                                it.weekdays.forEach{
                                    d =  when (it) {
                                        0 -> "$d Sun,"
                                        1 -> "$d Mon,"
                                        2 -> "$d Tue,"
                                        3 -> "$d Wed,"
                                        4 -> "$d Thu,"
                                        5 -> "$d Fri,"
                                        6 -> "$d Sat,"
                                        else -> d
                                    }
                                }
                                viewModel.ruleConditionDisplay.value = "Timer - ${(r.timer?.time ?: 0) / 60} : ${(r.timer?.time ?: 0) % 60 } - ${d.dropLast(1)}"
                            }

                        }

                        else -> {

                        }
                    }
                }
                viewModel.ruleAction.value = it.rule_data.rule_action.also {
                    if (it.scenes_list.size > 0) {
                        viewModel.ruleActionDisplay.value = "Scene"
                        try {
                            val scene = CacheSceneTwoWay.getSceneData(CacheHubData.selectedMacID)
                            var l = ""
                            scene?.value?.let {s ->

                                it.scenes_list.forEach { id ->
                                    s.forEach {
                                        if (id == it.scene_id) {
                                            CacheSceneTwoWay.getSceneItem(it.scenename_id).also {
                                                if (it.scene_name.length > 2) {
                                                    l = "$l ${it.scene_name},"
                                                }
                                            }
                                        }
                                    }
                                }
                                viewModel.ruleActionDisplay.value = l.dropLast(1)
                            }


                        }
                        catch (e : Exception) {
                            AppServices.log ("TronX", "Rules edit")
                        }
                    }
                    else {
                        viewModel.ruleActionDisplay.value = "Switch"
                        try {
                            if (it.device_list.size > 0) {
                                it.device_list[0].let { sensor ->
                                    CacheSceneTwoWay.getBoardData(
                                        CacheHubData.selectedMacID,
                                        sensor.thing_id
                                    )?.let { b ->
                                        b.switchesList.filter {
                                            it.switchId == sensor.switchId
                                        }.let {
                                            if (it.isNotEmpty()) {
                                                val t =
                                                    "${b.thing_name} - ${it[0].switchName} : ${if (sensor.switchstatus == 0) "Off" else "On"}"
                                                viewModel.ruleActionDisplay.value = t
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        catch (e : Exception) {
                            AppServices.log("Tronx", "Rule Edit - ${e.message}")
                        }
                    }
                }
                viewModel.condition.value = RuleCondition.valueOf(it.rule_type)
            }
        }
    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : RuleTypeSelectionFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.rule_type_selection_fragment, container, false)

        viewModel.ruleNameId.value?.let {
            if (it > 0) {
                val ruleItem = RuleDataRepository.getRuleItem(it)
                if (ruleItem.image != null) {
                    binding.imageView29.setImageBitmap(ruleItem.image)
                }
                else {
                    binding.imageView29.setImageResource(R.drawable.place_holder_2)
                }
            }
        }

        val rules = RuleDataRepository.getRuleList(CacheHubData.selectedMacID)

        val ruleItem = RuleDataRepository.getRuleItemList()
        val adapter = SelectRuleItemAdapter (requireContext(), ruleItem) {
            var f = true
            rules?.value?.forEach {r->
                if (it.rulename_id == r.rulename_id) {
                    f = false
                }
            }
            if (f) {
                viewModel.ruleName.value = it.rule_name
                viewModel.ruleNameId.value = it.rulename_id
                if (it.image != null) {
                    binding.imageView29.setImageBitmap(it.image)
                } else {
                    binding.imageView29.setImageResource(R.drawable.place_holder_2)
                }
                viewModel.updateView(viewModel.ruleCreateView)
            }
            else {
                AppDialogs.openMessageDialog(requireContext(), "Name already exist.")
            }
        }

        binding.ruleItemRecycleView.adapter = adapter

        viewModel.condition.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.textView196.text = "Add Condition"
            }
            else {
                binding.textView196.text = "Edit Condition"
            }
        })

        viewModel.ruleAction.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.textView198.text = "Add Action"
            }
            else {
                binding.textView198.text = "Edit Action"
            }
        })

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    when (viewModel.action) {
                        "Exit" -> {
                            requireActivity().finish()
                        }

                        "Back" -> {
                            findNavController().navigateUp()
                        }

                        "selectCondition" -> {
                            findNavController().navigate(R.id.action_ruleTypeSelection_to_selectRuleCondition)
                        }

                        "CreateItem" -> {
                            val d = RuleTypeSelectionDirections.actionRuleTypeSelectionToCreateArea4("Rule", "Create")
                            findNavController().navigate(d)
                        }


                        "selectAction" -> {
                            if (viewModel.condition.value != null) {
                                findNavController().navigate(R.id.action_ruleTypeSelection_to_ruleSelectAction)
                            }
                            else {
                                AppServices.toastShort(requireContext(),"Please add condition first")
                            }
                        }

                        "SaveRule" -> {
                            try {
                                if (args.ruleId > 0) viewModel.updateRule(
                                    requireContext(),
                                    args.ruleId
                                )
                                else viewModel.saveRule(requireContext())
                            }
                            catch (e : Exception) {
                                AppServices.log("TronX _ Rule", e.message.toString())
                            }
                        }

                        else -> {

                        }
                    }
                    viewModel.action = ""
                }
            }
        })


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

}