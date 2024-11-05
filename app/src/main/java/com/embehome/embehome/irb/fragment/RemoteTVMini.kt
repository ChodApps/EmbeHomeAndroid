package com.embehome.embehome.irb.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.embehome.embehome.Activity.RemoteList
import com.embehome.embehome.R
import com.embehome.embehome.databinding.*
import com.embehome.embehome.irb.RemoteCategoryEnum
import com.embehome.embehome.irb.model.RemoteCloudModel
import com.embehome.embehome.irb.viewmodel.RemoteMiniTVViewModel


class RemoteTVMini (val remote : MutableLiveData <RemoteCloudModel>): Fragment() {

    val viewModel : RemoteMiniTVViewModel by viewModels()
    val category : RemoteCategoryEnum = RemoteCategoryEnum.valueOf(remote.value?.ir_category ?: "TV")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (remote.value != null) {
//            category = RemoteCategoryEnum.valueOf(remote.value?.ir_category ?: "TV")
        }
        else {
//            category = RemoteCategoryEnum.TV
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = when (category) {
            RemoteCategoryEnum.TV -> {
                (DataBindingUtil.inflate(inflater, R.layout.fragment_remote_t_v_mini, container, false) as FragmentRemoteTVMiniBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.TABLE_FAN -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_table_fan_mini_fragment, container, false) as RemoteTableFanMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }

            RemoteCategoryEnum.DISH -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_setup_box_mini_fragment, container, false) as RemoteSetupBoxMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.DVD -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_d_v_d_player_mini_fragment, container, false) as RemoteDVDPlayerMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.SPEAKERS -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_audio_system_mini_fragment, container, false) as RemoteAudioSystemMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.AC -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_a_c_mini_fragment, container, false) as RemoteACMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.PROJECTOR -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_projector_mini_fragment, container, false) as RemoteProjectorMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.AIR_PURIFIER -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_air_purifier_mini_fragment, container, false) as RemoteAirPurifierMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.COOLER -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_cooler_mini_fragment, container, false) as RemoteCoolerMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.FAN -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_ceiling_fan_mini_fragment, container, false) as RemoteCeilingFanMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.GEYSER -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_geyser_mini_fragment, container, false) as RemoteGeyserMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }
            RemoteCategoryEnum.OVEN -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_oven_mini_fragment, container, false) as RemoteOvenMiniFragmentBinding).also {
                    it.viewModel = viewModel
                }
            }

        }

//        val binding : FragmentRemoteTVMiniBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_remote_t_v_mini, container, false)
//        DataBindingUtil.inflate(inflater, R.layout.remote_table_fan_mini_fragment, container, false) as RemoteTableFanMiniFragmentBinding
//        DataBindingUtil.inflate(inflater, R.layout.remote_setup_box_mini_fragment, container, false) as RemoteSetupBoxMiniFragmentBinding
//        DataBindingUtil.inflate(inflater, R.layout.remote_projector_mini_fragment, container, false) as RemoteProjectorMiniFragmentBinding
//        DataBindingUtil.inflate(inflater, R.layout.remote_oven_mini_fragment, container, false) as RemoteOvenMiniFragmentBinding
//        DataBindingUtil.inflate(inflater, R.layout.remote_geyser_mini_fragment, container, false) as RemoteGeyserMiniFragmentBinding
//        DataBindingUtil.inflate(inflater, R.layout.remote_d_v_d_player_mini_fragment, container, false) as RemoteDVDPlayerMiniFragmentBinding
//        DataBindingUtil.inflate(inflater, R.layout.remote_cooler_mini_fragment, container, false) as RemoteCoolerMiniFragmentBinding
//        DataBindingUtil.inflate(inflater, R.layout.remote_ceiling_fan_mini_fragment, container, false) as RemoteCeilingFanMiniFragmentBinding
//        DataBindingUtil.inflate(inflater, R.layout.remote_audio_system_mini_fragment, container, false) as RemoteAudioSystemMiniFragmentBinding


        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
//                if (remote.value != null) {
//                    if (remote.value!!.ir_data.size > 0 ) {
//                        val ir : String = remote.value!!.ir_data[viewModel.action] ?: ""
//                        if (ir.length > 1) {
//                            viewModel.play(CacheHubData.selectedHubIP, remote.value!!.thing_id, ir)
//                            Toast.makeText(requireContext(), "playing ${viewModel.action.substring(4)}", Toast.LENGTH_SHORT).show()
//                        }
//                        else
//                            Toast.makeText(requireContext(), "You did not perform IR read operation for this button at the time of creation", Toast.LENGTH_SHORT).show()
//                    }
//                    else
//                        Toast.makeText(requireContext(), "You have not done any read operation for this remote", Toast.LENGTH_SHORT).show()
//                }

                when (viewModel.action) {
                    "ClickButton" -> {
                        if (viewModel.button != null) {
                            viewModel.action(
                                requireContext(),
                                viewModel.button?.name.toString(),
                                remote.value?.ir_data?.get(viewModel.button!!.name) ?: "", "Operate", remote.value?.thing_id ?: 0)
                        }
                    }

                    "Full" -> {
                        remote.value?.let {
                            val i = Intent(requireActivity(), RemoteList::class.java)
                            i.putExtra("sno",it.thing_serial_number)
                            i.putExtra("remoteId", it.remote_id)
                            requireContext().startActivity(i)
                        }
                    }

                    else -> {
                        Toast.makeText(requireContext(), viewModel.action, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })



//        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}
