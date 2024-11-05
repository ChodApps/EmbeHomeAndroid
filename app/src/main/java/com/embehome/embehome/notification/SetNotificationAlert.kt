package com.embehome.embehome.notification

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
import androidx.lifecycle.Observer
import com.embehome.embehome.Model.SwitchAlertData
import com.embehome.embehome.Model.SwitchDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentSetNotificationAlertBinding

/**
 * A simple [Fragment] subclass.
 */
class SetNotificationAlert (
    val macID :String,
    val thingsID : Int,
    val switchID :Int,
    val name : String
) : Fragment() {

    val viewModel : SetNotificationAlertViewModel by viewModels ()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.switchName.value = name
        viewModel.macID = macID
        viewModel.thingsID = thingsID
        viewModel.switchId = switchID - 1
        viewModel.board = CacheSceneTwoWay.getBoardData(macID, thingsID)
        viewModel.sno = viewModel.board?.thing_serial_number!!
        val switch : SwitchDetails = viewModel. board!!.switchesList.filter {
            it.switchId == switchID
        }[0]
        if (switch.alert_data != null) {
            val alert : SwitchAlertData = switch.alert_data!!
            viewModel.switchStatus.value = alert.switchstatus != 0
            viewModel.repeat.value = alert.repeat_alert != 0
            val min : Int = alert.time_interval

            viewModel.btnText.value = viewModel.delete
            viewModel.hour.value = min / 60
            viewModel.min.value = min % 60
        }
        else {
            viewModel.btnText.value = viewModel.create
            viewModel.createMode.value = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding : FragmentSetNotificationAlertBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_set_notification_alert, container, false)

        val hour : NumberPicker = binding.numberPickerHour
        val min : NumberPicker= binding.numberPickerMinutes
        hour.wrapSelectorWheel = false
        min.wrapSelectorWheel = false

        if (viewModel.btnText.value == viewModel.delete) {
            hour.displayedValues = arrayOf(viewModel.hour.value.toString())
            min.displayedValues = arrayOf(viewModel.min.value.toString())
        }
        else {
            try {
                hour.maxValue = 65475
                min.maxValue = 59
                hour.value = viewModel.hour.value!!
                min.value = viewModel.min.value!!
            } catch (e: Exception) {
                //Log
            }
        }
        hour.setOnValueChangedListener { p0, old, new->
            Log.d("TronX", "$old $new")
            viewModel.hour.value = new
        }
        min.setOnValueChangedListener { p0, old, new->
            Log.d("TronX", "$old $new")
            viewModel.min.value = new
        }

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    SetAlert.Back -> {
                        requireActivity().finish()
                    }
                    SetAlert.Toast -> {
                        Toast.makeText(requireContext(), viewModel.toastString, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}
