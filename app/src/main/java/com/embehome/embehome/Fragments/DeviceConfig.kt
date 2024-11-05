package com.embehome.embehome.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.embehome.embehome.Adapter.ConfigSSBAdapter
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.SwitchDetails
import com.embehome.embehome.R
import com.embehome.embehome.Repository.DeviceHandleRepository
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.CacheHubData
import kotlinx.android.synthetic.main.fragment_device_config.view.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DeviceConfig (val device : BoardDetails) : Fragment() {

    val s = ArrayList<SwitchDetails>().apply {
        device.switchesList.forEach {
            add(SwitchDetails(it.switchId,it.switchName, it.switchType, it.switchstatus, it.switchLevel, it.switchIconId, it.scenesList, it.twoway_id, it.alert_data, it.switchWattage))
        }
    }
    val d = BoardDetails(device.thing_serial_number, device.thing_type, device.category, device.area_id, device.thing_name, device.thing_image,device.thing_id, s, device.thing_version)

    lateinit var adapter : ConfigSSBAdapter

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_device_config, container, false)
        adapter  = ConfigSSBAdapter(requireContext(), d)
        when (device.thing_type) {
            "SSB", "ISB", "RSB" -> {
                v.configBoard.adapter = adapter
                v.textView176.text = "CONFIGURE SSB"
                v.ssb_config.visibility = View.VISIBLE
                v.curtain_config.visibility = View.GONE
                v.button40.isEnabled = false
            }
            "CUR" -> {
                v.textView176.text = "CONFIGURE CURTAIN"
                v.ssb_config.visibility = View.GONE
                v.curtain_config.visibility = View.VISIBLE
                v.button39.isEnabled = false
            }
            else -> requireActivity().onBackPressed()
        }


        v.imageView235.setOnClickListener {
            requireActivity().onBackPressed()
        }

        v.button39.setOnClickListener {
            if (adapter.error.size > 0)
                AppDialogs.openMessageDialog(it.context, "Please Enter Valid Names")
            else {
                GlobalScope.launch (Main) {
                    AppDialogs.startLoadScreen(it.context)
                    val res = DeviceHandleRepository.updateDeviceDetails(
                        it.context,
                        CacheHubData.selectedMacID,
                        d
                    )
                    AppDialogs.stopLoadScreen()
                    if (res) {
                        device.switchesList.clear()
                        device.switchesList.addAll(s)
                        requireActivity().finish()
                    }
                }
            }
        }

        v.config_curtain_stop.setOnClickListener {
            config(device.thing_id, 200)
        }

        v.config_curtain_open.setOnClickListener {
            config(device.thing_id, 100)
        }

        v.config_curtain_close.setOnClickListener {
            config(device.thing_id, 0)
        }

        v.re_config_curtain_stop.setOnClickListener {
            reConfig(device.thing_id, 200)
        }

        v.re_config_curtain_open.setOnClickListener {
            reConfig(device.thing_id, 100)
        }

        v.re_config_curtain_close.setOnClickListener {
            reConfig(device.thing_id, 0)
        }

        v.button40.setOnClickListener {
            /*AppDialogs.openTitleDialog(requireContext(),"Save Configuration", "Are you sure you want to save the configuration ?","NO","YES"){dialog, which ->
                requireActivity().finish()
            }*/
            requireActivity().finish()
        }

        return v
    }

    @ExperimentalStdlibApi
    fun config (deviceID : Int, status: Int) {
        GlobalScope.launch (Main) {
            LANManager.configCurtain(deviceID, status)
        }
    }

    @ExperimentalStdlibApi
    fun reConfig (deviceID : Int, status: Int) {
        GlobalScope.launch (Main) {
            LANManager.reConfigCurtain(deviceID, status)
        }
    }
}