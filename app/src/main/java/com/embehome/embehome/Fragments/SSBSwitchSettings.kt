package com.embehome.embehome.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.embehome.embehome.Model.SwitchDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.SSBSwitchSettingsFragmentBinding
import com.embehome.embehome.getSwitchImages
import com.embehome.embehome.instructions.SwitchIconsList
import com.embehome.embehome.viewModel.SSBSwitchSettingsViewModel

class SSBSwitchSettings (val macID : String, val thingsID : Int, val switchID : Int): Fragment() {



    val  viewModel: SSBSwitchSettingsViewModel by viewModels()
    var switch : SwitchDetails? = null
    val icon  = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : SSBSwitchSettingsFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.s_s_b_switch_settings_fragment, container, false)
        val hour : NumberPicker = binding.numberPickerHour
        val min : NumberPicker = binding.numberPickerMin
        hour.wrapSelectorWheel = false
        min.wrapSelectorWheel = false

        viewModel.firstTime.observe(viewLifecycleOwner, Observer {
            if (it) {
                val b  = CacheSceneTwoWay.getBoardData(macID, thingsID)
                if (b != null) {
                    viewModel.board.value = b
                }
                else
                    requireActivity().finish()
            }
        })

        viewModel.board.observe(viewLifecycleOwner, Observer {
            if (it != null && (it.thing_type == "SSB" || it.thing_type == "ISB" || it.thing_type == "RSB")) {
                switch =  it.switchesList.filter { it.switchId == switchID } [0]
                viewModel.switchName.value = switch?.switchName
                viewModel.powerNumber.value = (switch?.switchWattage ?: 0).toString()
                viewModel.switchID = switch?.switchId!!
                switch?.let {
                    binding.imageView95.setImageResource(getSwitchImages(it.switchIconId, false))
                }
                viewModel.sno = it.thing_serial_number
                viewModel.thingsID = it.thing_id
                switch?.alert_data.also {s->
                    if (s != null) {
                        viewModel.alertStatus.value = s.switchstatus > 0
                        viewModel.alertRepeat.value = s.repeat_alert > 0
                        viewModel.alert.value = true
                        viewModel.alertBtn.value = viewModel.delete
                        viewModel.hour.value = s.time_interval / 60
                        viewModel.min.value = s.time_interval % 60
                        viewModel.alertID = s.alert_id
                    }
                    else {
                        viewModel.alert.value = false
                        viewModel.alertBtn.value = viewModel.create
                        hour.maxValue = 65475
                        min.maxValue = 59
                    }
                }
            }
        })

        viewModel.alert.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.alertTime.value = "${viewModel.hour.value} hour  ${viewModel.min.value} min"
            }
        })

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {

                    "removeFocus" -> {
                        view?.clearFocus()
                        AppServices.fragHideSoftKeyBoard(requireContext(), view)
                        val b = viewModel.board.value
                        val s = b?.switchesList?.filter { it.switchId == switchID }!![0]
                        viewModel.updateSwitchDetails(
                            b?.thing_serial_number!!,
                            switchID,
                            viewModel.switchName.value!!,
                            s.switchType,
                            icon.value ?: s.switchIconId,
                            viewModel.powerNumber.value!!.toInt()
                        )
                    }

                    "Back" -> {
                        requireActivity().finish()
                    }

                    "Toast" -> {
                        Toast.makeText(requireContext(), viewModel.toastString, Toast.LENGTH_SHORT).show()
                        viewModel.toastString = ""
                    }

                    "DeleteAlert" -> {
                        switch?.alert_data = null
                    }

                    else -> {
                        Toast.makeText(requireContext(), viewModel.action, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.action = ""
            }
        })

        hour.setOnValueChangedListener { p0, old, new->
            Log.d("TronX", "$old $new")
            viewModel.hour.value = new
        }
        min.setOnValueChangedListener { p0, old, new->
            Log.d("TronX", "$old $new")
            viewModel.min.value = new
        }

        binding.textView234.setOnClickListener {
            val d = SwitchIconsList(icon)
            d.show(requireActivity().supportFragmentManager, "Icons")
        }

        icon.observe(viewLifecycleOwner, Observer {
            Log.d("TronX _ Icon", it.toString())
            it?.let {
                binding.imageView95.setImageResource(getSwitchImages(it, false))
            }
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }



}