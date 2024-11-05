package com.embehome.embehome.rules.fragment

import android.os.Bundle
import android.util.Log
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
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.databinding.RuleAddTimeFragmentBinding
import com.embehome.embehome.rules.viewModels.RuleAddTimeViewModel
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel
import java.util.*

class RuleAddTime : Fragment() {

    private val tempViewModel: RuleAddTimeViewModel by viewModels ()
    private val viewModel : RuleTypeSelectionViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.ruleDetail.value?.timer?.let {
            if (it.time > 0) {
                tempViewModel.weekDays.addAll(it.weekdays)
                tempViewModel.setWeekDay()
                tempViewModel.timeRepeat.value = it.repeat
            }
        }
        val w = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        if  (tempViewModel.weekDays.size <= 0 ) tempViewModel.addWeekDay(w - 1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : RuleAddTimeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.rule_add_time_fragment, container, false)

        val hour = binding.numberPicker
        val min = binding.numberPicker1
        hour.wrapSelectorWheel = false
        hour.maxValue = 23
        min.maxValue = 59

        hour.displayedValues = Array<String>(24){
            if (it < 10)
                "0$it"
            else it.toString()
        }
        min.displayedValues = Array<String>(60){
            if (it < 10)
                "0$it"
            else it.toString()
        }
        hour.value = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        min.value = Calendar.getInstance()[Calendar.MINUTE]

        hour.value.let {h -> 
            min.value.let {m->
                tempViewModel.time.value = h * 60 + m
            }
        }

        viewModel.ruleDetail.value?.timer?.let {
            if (it.time > 0) {
                val t = it.time
                hour.value = t / 60
                min.value = t % 60

            }
        }

        hour.setOnValueChangedListener { picker, oldVal, newVal ->
            val h = newVal
            val m = min.value
            tempViewModel.time.value = (h*60 + m).also {
                Log.d("TronX", "time selected - $it")
            }
        }

        min.setOnValueChangedListener { picker, oldVal, newVal ->
            val h = newVal
            val m = hour.value
            tempViewModel.time.value = (m*60 + h).also {
                Log.d("TronX", "time selected - $it")
            }
        }

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                when (viewModel.action) {

                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "setConditionTime" -> {
                        if (tempViewModel.weekDays.isEmpty()) {
                            AppServices.toastShort(requireContext(), "Please select at least one DAY")
                        }
                        else {
                            tempViewModel.weekDays.sort()
                            viewModel.setConditionTime(
                                requireContext(),
                                tempViewModel.weekDays,
                                tempViewModel.time.value ?: 0,
                                tempViewModel.timeRepeat.value ?: false
                            )
                            findNavController().navigateUp()
                        }
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